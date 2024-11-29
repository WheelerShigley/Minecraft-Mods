package me.wheelershigley.treeinaforest;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.UUID;

public class TreeInAForest implements ModInitializer {
    public static final String MOD_ID = "tree_in_a_forest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ArrayList<UUID> players = new ArrayList<>();

    @Override
    public void onInitialize() {
        updateServerTicking();
    }

    public static void updateServerTicking() {
        if(players.size() <= 0) {
            LOGGER.info("Stopping time.");
        } else {
            LOGGER.info("Starting time.");
        }
    }
}
