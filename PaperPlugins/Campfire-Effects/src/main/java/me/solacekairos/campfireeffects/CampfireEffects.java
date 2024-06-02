package me.solacekairos.campfireeffects;

import me.solacekairos.campfireeffects.event_listeners.breakCampfire;
import me.solacekairos.campfireeffects.event_listeners.placeCampfire;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public final class CampfireEffects extends JavaPlugin {
    public final String plugin_name = "campfire_effects";
    public Logger campfire_effects_logger;
    public placeCampfire placeEvent; public breakCampfire breakEvent;

    @Override
    public void onEnable() {
        campfire_effects_logger = this.getLogger();
        campfire_effects_logger.info("Campfires now give Effects!");

        saveDefaultConfig();
        placeEvent = new placeCampfire(this);
        breakEvent = new breakCampfire(this);
        Bukkit.getPluginManager().registerEvents(placeEvent, this);
        Bukkit.getPluginManager().registerEvents(breakEvent, this);

    }

    @Override
    public void onDisable() {
        campfire_effects_logger.info("Campfires nolonger give Effects!");
    }
}
