package me.wheelershigley.tree_in_a_forest;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.level.ServerWorldProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TreeInAForest implements ModInitializer {
    public static final String MOD_ID = "tree_in_a_forest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static MinecraftServer server = null;

    @Override
    public void onInitialize() {
        Registrations.registerPostServerStartUp();
    }

    private static float rate = 20.0f;
    public static void updateServerTicking() {
        if(server == null) {
            return;
        }

        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        Iterable<ServerWorld> worlds = server.getWorlds();

        if(players.size() <= 0) {
            logTranslatableMessage(
                "tree_in_a_forest.text.stopping_time"
            );
            rate = server.getTickManager().getTickRate();
            server.getTickManager().setTickRate(0.0f);
        } else {
            logTranslatableMessage(
                "tree_in_a_forest.text.starting_time"
            );
            server.getTickManager().setTickRate(rate);
        }
    }

    private static void logTranslatableMessage(String key, Object... arguments) {
        LOGGER.info(
            Text.literal(
                Text.translatable(key, arguments).getString()
            ).getString()
        );
    }
}
