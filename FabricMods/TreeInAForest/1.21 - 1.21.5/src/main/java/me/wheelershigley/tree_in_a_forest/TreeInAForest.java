package me.wheelershigley.tree_in_a_forest;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.UUID;

public class TreeInAForest implements ModInitializer {
    public static final String MOD_ID = "tree_in_a_forest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ArrayList<UUID> players = new ArrayList<>();
    public static ArrayList<ServerWorld> worlds = new ArrayList<>();

    private void onServerStart(ServerWorld world) {
        if( worlds.contains(world) ) { return; }
        TreeInAForest.worlds.add(world);
        updateServerTicking();
    }

    @Override
    public void onInitialize() {
        ServerTickEvents.START_WORLD_TICK.register(this::onServerStart);
    }

    public static void updateServerTicking() {
        if(players.size() <= 0) {
            LOGGER.info("Stopping time.");
            for(ServerWorld world : worlds) {
                world.getServer().getTickManager().setTickRate(0.0f);
            }
        } else {
            LOGGER.info("Starting time.");
            for(ServerWorld world : worlds) {
                world.getServer().getTickManager().setTickRate(20.0f);
            }
        }
    }
}
