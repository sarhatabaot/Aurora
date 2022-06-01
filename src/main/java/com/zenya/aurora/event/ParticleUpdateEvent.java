package com.zenya.aurora.event;

import com.zenya.aurora.util.LocationTools;
import com.zenya.aurora.util.ChunkContainer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ParticleUpdateEvent extends Event implements Cancellable {

    private boolean isCancelled;
    private Player player;

    public ParticleUpdateEvent(Player player) {
        this.isCancelled = false;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public ChunkContainer getChunk() {
        return (new ChunkContainer()).fromLocation(player.getLocation());
    }

    public World getWorld() {
        return player.getWorld();
    }

    public ChunkContainer getPreviousChunk() {
        double relativeX = player.getVelocity().getX();
        double relativeZ = player.getVelocity().getZ();
        if (relativeX > 0) {
            relativeX = 1;
        } else {
            relativeX = -1;
        }
        if (relativeZ > 0) {
            relativeZ = 1;
        } else {
            relativeZ = -1;
        }

        return new ChunkContainer(player.getWorld(), getChunk().getX() + (int) relativeX, getChunk().getZ() + (int) relativeZ);
    }

    public ChunkContainer[] getNearbyChunks(int radius) {
        return LocationTools.getSurroundingChunks(getChunk(), radius);
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    //Default custom event methods
    private static final HandlerList handlers = new HandlerList();

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
