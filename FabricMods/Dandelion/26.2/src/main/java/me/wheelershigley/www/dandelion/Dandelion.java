package me.wheelershigley.www.dandelion;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.wheelershigley.www.dandelion.config.SeenPlayers;
import me.wheelershigley.www.dandelion.registerations.Creative;
import me.wheelershigley.www.dandelion.registerations.Events;
import me.wheelershigley.www.dandelion.registerations.ModItems;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Dandelion implements ModInitializer {
    public static final String MOD_ID = "dandelion";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static SeenPlayers SEEN_PLAYERS;

    @Override
    public void onInitialize() {
        ModItems.initialize();
        Creative.registerItems();

        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.markAsRequired();

        SEEN_PLAYERS = SeenPlayers.load();
        Events.register();
    }
}
