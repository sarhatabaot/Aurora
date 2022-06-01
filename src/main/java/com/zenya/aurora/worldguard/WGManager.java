package com.zenya.aurora.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WGManager {

    public static final WGManager INSTANCE = new WGManager();

    public static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        return (!(plugin instanceof WorldGuardPlugin)) ? null : (WorldGuardPlugin) plugin;
    }

    public ApplicableRegionSet getApplicableRegionSet(Player player) {
        return getApplicableRegionSet(player.getLocation());
    }

    public RegionManager getRegionManager(World world) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
    }

    public ApplicableRegionSet getApplicableRegionSet(Location loc) {
        return getRegionManager(loc.getWorld()).getApplicableRegions(BukkitAdapter.asBlockVector(loc));
    }

    public <T extends Flag<?>> T registerFlag(T flag) throws FlagConflictException {
        WorldGuard.getInstance().getFlagRegistry().register(flag);
        return flag;
    }

}
