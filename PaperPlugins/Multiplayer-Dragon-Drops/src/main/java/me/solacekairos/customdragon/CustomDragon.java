package me.solacekairos.customdragon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public final class CustomDragon extends JavaPlugin {

    public Logger dragon_logger;
    public DeathListener listener;
    public Reload reload;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Plugin startup logic
        dragon_logger = getLogger();
        listener = new DeathListener(this);
        reload = new Reload(listener);

        dragon_logger.info("Dragons improved");

        PluginManager manager = Bukkit.getPluginManager();
        //events added:
        {
            manager.registerEvents(listener, this);
        }

        //commands added:
        {
            getCommand("dragons").setExecutor( new Reload(listener) );
            getCommand("dragons").setTabCompleter( new Reload(listener) );
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        dragon_logger.info("Dragons unimproved");
    }
}
