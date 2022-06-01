package com.zenya.aurora.scheduler;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.event.ParticleUpdateEvent;
import com.zenya.aurora.util.ChunkContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class TrackLocationTask implements AuroraTask {

    public static final TrackLocationTask INSTANCE = new TrackLocationTask();
    private BukkitTask[] runnables;
    private CompletableFuture<HashMap<Player, Location>> playerCoords;

    public TrackLocationTask() {
        playerCoords = CompletableFuture.supplyAsync(HashMap::new);
        runTasks();
    }

    @Override
    public TaskKey getKey() {
        return TaskKey.TRACK_LOCATION_TASK;
    }

    @Override
    public void runTasks() {
        //Task to fire events
        BukkitTask task1 = new BukkitRunnable() {
            @Override
            public void run() {
                playerCoords.thenAcceptAsync(coordMap -> {
                    if (!Bukkit.getOnlinePlayers().isEmpty()) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            //Chunk change
                            Location curr = player.getLocation();
                            Location old = coordMap.getOrDefault(player, new Location(player.getWorld(), 0, 0, 0));
                            if (!Arrays.equals(new ChunkContainer().fromLocation(curr).getChunkCoords(),
                                    new ChunkContainer().fromLocation(old).getChunkCoords())) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(player));
                                    }
                                }.runTask(Aurora.getInstance());
                                //Force update
                                coordMap.put(player, player.getLocation());
                            }
                        }
                    }
                });
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, 20 * (long) 3);

        //Task to update location map
        BukkitTask task2 = new BukkitRunnable() {
            @Override
            public void run() {
                playerCoords.thenAcceptAsync(coordMap -> {
                    if (!Bukkit.getOnlinePlayers().isEmpty()) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (!coordMap.containsKey(player)) {
                                //Initialise player location
                                coordMap.put(player, new Location(player.getWorld(), 0, 0, 0));
                            } else {
                                //Update player location
                                coordMap.put(player, player.getLocation());
                            }
                        }
                    }
                });
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, 20 * 5);

        //Task to remove old player entries
        BukkitTask task3 = new BukkitRunnable() {
            @Override
            public void run() {
                playerCoords.thenAcceptAsync(coordMap -> {
                    if (!Bukkit.getOnlinePlayers().isEmpty()) {
                        if (coordMap.keySet() != null && !coordMap.keySet().isEmpty()) {
                            coordMap.entrySet().removeIf(entry -> (!Bukkit.getOnlinePlayers().contains(entry.getKey())));
                        }
                    } else {
                        coordMap.clear();
                    }
                });
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, 20 * 10);

        //Add to runnables[]
        runnables = new BukkitTask[]{task1, task2, task3};
    }

    @Override
    public BukkitTask[] getTasks() {
        return runnables;
    }
}
