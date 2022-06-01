package com.zenya.aurora;

import co.aikar.commands.PaperCommandManager;
import com.zenya.aurora.api.AuroraAPI;
import com.zenya.aurora.api.ParticleFactory;
import com.zenya.aurora.command.AuroraCommand;
import com.zenya.aurora.util.ext.LightUtil;
import com.zenya.aurora.event.Listeners;
import com.zenya.aurora.util.ext.ZParticle;
import com.zenya.aurora.storage.ParticleFileCache;
import com.zenya.aurora.storage.ParticleFileManager;
import com.zenya.aurora.storage.StorageFileManager;
import com.zenya.aurora.storage.TaskManager;
import com.zenya.aurora.storage.ParticleManager;
import com.zenya.aurora.util.Logger;
import com.zenya.aurora.worldguard.AmbientParticlesFlag;
import com.zenya.aurora.worldguard.WGManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class Aurora extends JavaPlugin {

    private static Aurora instance;
    private LightUtil lightAPI;
    private TaskManager taskManager;
    private StorageFileManager storageFileManager;
    private ParticleFileManager particleFileManager;
    private ParticleFileCache particleFileCache;
    private AmbientParticlesFlag ambientParticlesFlag;

    @Override
    public void onLoad() {
        //WorldGuard dependency
        if (WGManager.getWorldGuard() != null) {
            try {
                ambientParticlesFlag = AmbientParticlesFlag.INSTANCE;
            } catch (Exception exc) {
                Logger.logError("PlugMan or /reload is not supported by Aurora");
                Logger.logError("If you're updating your particle configs, use /aurora reload");
                Logger.logError("If you're updating the plugin version, restart your server");
                this.getServer().getPluginManager().disablePlugin(this);
            }
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        // Enables Metrics
        new Metrics(this, 12646);

        //Register API
        AuroraAPI.setAPI(new AuroraAPIImpl());

        //Init config, messages, biomes and db
        storageFileManager = StorageFileManager.INSTANCE;

        //Init LightAPI
        if (StorageFileManager.getConfig().getBool("enable-lighting")) {
            lightAPI = LightUtil.INSTANCE;
        }

        //Init particle files
        particleFileManager = ParticleFileManager.INSTANCE;
        particleFileCache = ParticleFileCache.INSTANCE;

        //Register all runnables
        //Spigot buyer ID check in here
        taskManager = TaskManager.INSTANCE;

        //Register events
        this.getServer().getPluginManager().registerEvents(new Listeners(), this);

        //Register commands
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new AuroraCommand());
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(instance);
        taskManager.unregisterTasks();
        ParticleManager pm = ParticleManager.INSTANCE;
        for (Player player : pm.getPlayers()) {
            pm.unregisterTasks(player, true);
        }
        try {
            LightUtil.disable();
        } catch (NoClassDefFoundError exc) {
            //Silence errors
        }
    }

    private static class AuroraAPIImpl extends AuroraAPI {

        private final ParticleFactory factory = new ZParticle();

        @Override
        public ParticleFactory getParticleFactory() {
            return factory;
        }
    }

    public static Aurora getInstance() {
        return instance;
    }
}
