package com.zenya.aurora.worldguard;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.RegionResultSet;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.storage.ParticleFileManager;
import com.zenya.aurora.util.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AmbientParticlesFlag {

    public static final AmbientParticlesFlag INSTANCE = new AmbientParticlesFlag();
    private SetFlag<String> flag;

    public AmbientParticlesFlag() {
        try {
            flag = WGManager.INSTANCE.registerFlag(new SetFlag<>("ambient-particles", new StringFlag(null)));
        } catch (FlagConflictException exc) {
            Logger.logError("Unable to register WorldGuard flag \"ambient-particles\"");
            exc.printStackTrace();
        }
    }

    public List<ParticleFile> getParticles(Player player) {
        return getParticles(player.getLocation());
    }

    public List<ParticleFile> getParticles(Location loc) {
        ProtectedRegion global = WGManager.INSTANCE.getRegionManager(loc.getWorld()).getRegion("__global__");
        return getParticles(WGManager.INSTANCE.getApplicableRegionSet(loc), global);
    }

    private List<ParticleFile> getParticles(ApplicableRegionSet regions, ProtectedRegion global) {
        List<ParticleFile> enabledParticles = new ArrayList<>();
        //Add __global__ region
        if (global != null) {
            List<ProtectedRegion> regionList = new ArrayList<>();
            regionList.add(global);
            regionList.addAll(regions.getRegions());
            regions = new RegionResultSet(regionList, null);
        }

        for (ProtectedRegion region : regions.getRegions()) {
            Set<String> regionFlag = region.getFlag(flag);
            if (regionFlag != null && !regionFlag.isEmpty()) {
                for (String particleName : regionFlag) {
                    ParticleFile particleFile = ParticleFileManager.INSTANCE.getParticleByName(particleName);
                    if (particleFile != null && !enabledParticles.contains(particleFile)) {
                        enabledParticles.add(particleFile);
                    }
                }
            }
        }
        return enabledParticles;
    }

    public SetFlag<String> getFlag() {
        return flag;
    }

}
