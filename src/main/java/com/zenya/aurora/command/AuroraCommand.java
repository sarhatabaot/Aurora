package com.zenya.aurora.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.zenya.aurora.Aurora;
import com.zenya.aurora.event.ParticleUpdateEvent;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.storage.ParticleFileCache;
import com.zenya.aurora.storage.ParticleFileManager;
import com.zenya.aurora.storage.StorageFileManager;
import com.zenya.aurora.storage.ToggleManager;
import com.zenya.aurora.util.ChatBuilder;
import com.zenya.aurora.util.ChunkContainer;
import com.zenya.aurora.util.LocationTools;
import com.zenya.aurora.util.ext.LightAPI;
import com.zenya.aurora.worldguard.AmbientParticlesFlag;
import com.zenya.aurora.worldguard.WGManager;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandAlias("aurora")
@Description("Main command for Aurora")
public class AuroraCommand extends BaseCommand {

    @Default
    @CatchUnknown
    @CommandAlias("help")
    @CommandPermission("aurora.command.help")
    public void onHelp(final CommandSender sender) {
        sendUsage(sender);
    }

    @Subcommand("toggle")
    @CommandPermission("aurora.command.toggle")
    public static class ToggleSubCommand extends BaseCommand {
        @Default
        public void onDefault(final Player player) {
            ChatBuilder chat = (new ChatBuilder()).withSender(player);
            chat.withPlayer(player);
            if (ToggleManager.INSTANCE.isToggled(player.getName())) {
                ToggleManager.INSTANCE.registerToggle(player.getName(), false);
                chat.sendMessages("command.toggle.disable");
            } else {
                ToggleManager.INSTANCE.registerToggle(player.getName(), true);
                chat.sendMessages("command.toggle.enable");
            }
            Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(player));
        }

        @Subcommand("on")
        public void onToggleOn(final Player player) {
            ChatBuilder chat = (new ChatBuilder()).withSender(player);
            ToggleManager.INSTANCE.registerToggle(player.getName(), true);
            chat.sendMessages("command.toggle.enable");
            Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(player));
        }

        @Subcommand("off")
        public void onToggleOff(final Player player) {
            ChatBuilder chat = (new ChatBuilder()).withSender(player);
            ToggleManager.INSTANCE.registerToggle(player.getName(), false);
            chat.sendMessages("command.toggle.disable");
            Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(player));
        }
    }


    @Subcommand("reload")
    @CommandPermission("aurora.command.reload")
    public void onReload(final CommandSender sender) {
        ChatBuilder chat = (new ChatBuilder()).withSender(sender);
        StorageFileManager.reloadFiles();
        if (!StorageFileManager.getConfig().getBool("enable-lighting")) {
            try {
                LightAPI.disable();
            } catch (NoClassDefFoundError exc) {
                // Already disabled, do nothing
            }
        }
        ParticleFileCache.reload();
        chat.withArgs(ParticleFileManager.INSTANCE.getParticles().size()).sendMessages("command.reload");

        for (Player p : Bukkit.getOnlinePlayers()) {
            Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(p));
        }
    }

    @Subcommand("status")
    @CommandPermission("aurora.command.status")
    public void onStatus(final CommandSender sender) {
        ChatBuilder chat = (new ChatBuilder()).withSender(sender);
        StringBuilder globalFiles = new StringBuilder();
        if (ParticleFileManager.INSTANCE.getParticles() == null || ParticleFileManager.INSTANCE.getParticles().size() == 0) {
            //No particle files
            globalFiles.append("None");
        } else {
            for (ParticleFile particleFile : ParticleFileManager.INSTANCE.getParticles()) {
                //Check if enabled or disabled
                String particleName = getParticleName(sender, particleFile);
                globalFiles.append(particleName);
            }
        }
        chat.withArgs(ParticleFileManager.INSTANCE.getParticles().size(), globalFiles.toString()).sendMessages("command.status");
    }

    private String getParticleName(CommandSender sender, ParticleFile particleFile) {
        //Check if enabled or disabled
        String particleName = particleFile.isEnabled() ? ChatBuilder.translateColor("&a") : ChatBuilder.translateColor("&c");
        //If enabled, check if active in region/biome
        if (particleFile.isEnabled() && sender instanceof Player player) {
            Biome biome = player.getLocation().getBlock().getBiome();
            String biomeName = biome.toString();

            //WG support
            if (WGManager.getWorldGuard() != null) {
                if (AmbientParticlesFlag.INSTANCE.getParticles(player).contains(particleFile) ||
                        (AmbientParticlesFlag.INSTANCE.getParticles(player).isEmpty() && ParticleFileCache.INSTANCE.getClass(biomeName).contains(particleFile))) {
                    //Set if particle is in region
                    particleName = ChatBuilder.translateColor("&b");
                }
                //No WG
            } else if (ParticleFileCache.INSTANCE.getClass(biomeName).contains(particleFile)) {
                //Only set if particle is in biome
                particleName = ChatBuilder.translateColor("&b");
            }

            //Check if disabled in world
            if (StorageFileManager.getConfig().listContains("disabled-worlds", player.getWorld().getName())) {
                particleName = ChatBuilder.translateColor("&c");
            }
        }
        particleName += (particleFile.getName() + ChatBuilder.translateColor("&f, "));
        return particleName;
    }


    @Subcommand("fixlighting")
    @CommandCompletion("@range:10")
    @CommandPermission("aurora.command.fixlighting")
    public void onFixLighting(final Player player, int chunks) {
        ChatBuilder chat = (new ChatBuilder()).withSender(player);

        if (chunks < 0 || chunks > 10) {
            sendUsage(player);
            return;
        }

        chat.sendMessages("command.fixlighting.start");
        new BukkitRunnable() {
            @Override
            public void run() {
                for (ChunkContainer container : LocationTools.getSurroundingChunks(player.getLocation().getChunk(), chunks)) {
                    container.getWorld().refreshChunk(container.getX(), container.getZ());
                }
                chat.withArgs(chunks, chunks).sendMessages("command.fixlighting.done");
            }
        }.runTask(Aurora.getInstance());

    }

    private void sendUsage(CommandSender sender) {
        ChatBuilder chat = (new ChatBuilder()).withSender(sender);
        chat.sendMessages("command.help");
    }
}
