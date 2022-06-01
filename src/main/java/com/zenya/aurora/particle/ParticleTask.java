package com.zenya.aurora.particle;

import com.zenya.aurora.api.ParticleFactory;
import com.zenya.aurora.storage.StorageFileManager;
import com.zenya.aurora.util.ext.ZParticleDisplay;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.scheduler.TaskKey;
import com.zenya.aurora.util.ext.LightUtil;
import com.zenya.aurora.util.RandomNumber;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.beykerykt.minecraft.lightapi.common.api.engine.LightFlag;

public abstract class ParticleTask {

    public BukkitTask[] runnables;
    public int locIndex = 0;

    public Player player;
    public Location[] locs;
    public Particle particle;
    public ZParticleDisplay display;
    public int maxCount;
    public boolean lighting;
    public RandomNumber<Double> rate;
    public RandomNumber<Integer> update;
    public RandomNumber<Long> duration;
    public RandomNumber<Double> rotationAngle;
    public char rotationAxis;

    public ParticleTask(Player player, Location[] locs, ParticleFile particleFile) {
        this.player = player;
        this.locs = locs;
        this.particle = Particle.valueOf(particleFile.getParticle().getParticleName());
        this.display = ParticleFactory.toDisplay(particle, player);
        this.maxCount = particleFile.getParticle().getMaxCount();
        this.lighting = StorageFileManager.getConfig().getBool("enable-lighting") && particleFile.getParticle().isEnableLighting();
        this.rate = particleFile.getProperties().getRate();
        this.update = particleFile.getProperties().getUpdate();
        this.duration = particleFile.getProperties().getDuration();
        this.rotationAngle = particleFile.getProperties().getRotationAngle();
        this.rotationAxis = particleFile.getProperties().getRotationAxis();
    }

    public abstract TaskKey getKey();

    public abstract BukkitTask generate();

    public Player getPlayer() {
        return player;
    }

    public BukkitTask[] getTasks() {
        return runnables;
    }

    public abstract void runTasks();

    public void killTasks(boolean isShutdown) {
        for (BukkitTask t : runnables) {
            t.cancel();
        }

        if (lighting) {
            for (Location loc : locs) {
                LightUtil.clearLight(loc, LightFlag.BLOCK_LIGHTING, true, isShutdown);
            }
        }
    }
}
