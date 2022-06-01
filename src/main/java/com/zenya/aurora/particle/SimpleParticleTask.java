package com.zenya.aurora.particle;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.util.ext.LightUtil;
import com.zenya.aurora.util.RandomNumber;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.beykerykt.minecraft.lightapi.common.api.engine.LightFlag;

import java.util.ArrayList;
import java.util.List;

//For particles tasks which require setting lighting at a singular location
public abstract class SimpleParticleTask extends ParticleTask {

    public RandomNumber<Double> length;
    public RandomNumber<Double> radius;

    public SimpleParticleTask(Player player, Location[] locs, ParticleFile particleFile) {
        super(player, locs, particleFile);
        this.length = particleFile.getProperties().getLength();
        this.radius = particleFile.getProperties().getRadius();
        this.runTasks();
    }

    @Override
    public void runTasks() {
        //List to track existing particle groups
        List<BukkitTask> drawTasks = new ArrayList<>();

        //Task to set particle groups
        BukkitTask task1 = new BukkitRunnable() {
            @Override
            public void run() {
                //Remove expired tasks
                if (!drawTasks.isEmpty()) {
                    drawTasks.removeIf(drawTask -> !Bukkit.getScheduler().isQueued(drawTask.getTaskId()) && !Bukkit.getScheduler().isCurrentlyRunning(drawTask.getTaskId()));
                }

                //Manage total displayed particle groups
                if (drawTasks.size() < maxCount) {
                    //Create particles
                    drawTasks.add(generate());

                    //Set lighting if enabled
                    if (lighting) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                LightUtil.setLight(locs[locIndex], LightFlag.BLOCK_LIGHTING, 15, true, false);
                            }
                        }.runTask(Aurora.getInstance());
                    }

                    //Go to next index
                    if (locIndex < (locs.length - 1)) {
                        locIndex++;
                    } else {
                        locIndex = 0;
                    }
                }
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, update.generateInt());

        //Add to runnables[]
        runnables = new BukkitTask[]{task1};
    }
}
