package me.wheelershigley.tree_in_a_forest;

import com.mojang.authlib.GameProfile;
import me.wheelershigley.tree_in_a_forest.command.Registrator;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.wheelershigley.tree_in_a_forest.helpers.MessagesHelper.sendConsoleInfoTranslatableMessage;

public class TreeInAForest implements ModInitializer {
    public static final String MOD_ID = "tree_in_a_forest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final HashMap<UUID, GameProfile> gameProfileCache = new HashMap<>();
    public static MinecraftServer server = null;

    /* TODO
     * Prevent blacklisted users from ticking time
     * - have a boolean (@here) which updates when players join&leave
     * - Mixin into some world-class and prevent time from being incremented if above boolean is false
     */

    @Override
    public void onInitialize() {
        Registrator.registerCommand();
        EventRegistrations.registerPostServerStartUp();
    }

    private static float rate = 20.0f;
    public static void updateServerTicking() {
        if(server == null) {
            return;
        }

        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        //TODO remember each world's tickrate
//        Iterable<ServerWorld> worlds = server.getWorlds();

        if(players.size() <= 0) {
            sendConsoleInfoTranslatableMessage(
                "tree_in_a_forest.text.stopping_time"
            );
            rate = server.getTickManager().getTickRate();
            server.getTickManager().setTickRate(0.0f);
        } else {
            sendConsoleInfoTranslatableMessage(
                "tree_in_a_forest.text.starting_time"
            );
            server.getTickManager().setTickRate(rate);
        }
    }
}
