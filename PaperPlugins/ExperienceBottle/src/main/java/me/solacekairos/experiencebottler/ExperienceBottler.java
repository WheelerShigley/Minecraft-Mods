package me.solacekairos.experiencebottler;

import org.bukkit.plugin.java.JavaPlugin;

public final class ExperienceBottler extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Added experience bottler");

        //register command:
        {
            xpb operations = new xpb();
            getCommand("exp").setExecutor(     operations );
            getCommand("exp").setTabCompleter( operations );
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Removed experience bottler");
    }
}
