package me.wheelershigley.tree_in_a_forest;

import com.mojang.authlib.GameProfile;
import me.wheelershigley.tree_in_a_forest.blacklist.Blacklist;
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
    public static boolean serverHasOnlyBlacklistedPlayers = false;

    @Override
    public void onInitialize() {
        Registrator.registerCommand();
        EventRegistrations.registerPostServerStartUp();
    }

    private static float tick_rate = 20.0f;
    public static void updateServerTicking() {
        if(server == null) {
            return;
        }

        boolean wasTimeStoppedBefore = TreeInAForest.serverHasOnlyBlacklistedPlayers;
        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        TreeInAForest.serverHasOnlyBlacklistedPlayers =
            players.isEmpty() || !doesServerHasNonBotOnline(players)
        ;
        LOGGER.info( Boolean.toString(serverHasOnlyBlacklistedPlayers) );

        if( players.isEmpty() ) {
            tick_rate = server.getTickManager().getTickRate();
            server.getTickManager().setTickRate(0.0f);
        } else {
            server.getTickManager().setTickRate(tick_rate);
        }

        if( TreeInAForest.serverHasOnlyBlacklistedPlayers) {
            if(!wasTimeStoppedBefore) {
                sendConsoleInfoTranslatableMessage(
                    "tree_in_a_forest.text.stopping_time"
                );
            }
        } else {
            if(wasTimeStoppedBefore) {
                sendConsoleInfoTranslatableMessage(
                    "tree_in_a_forest.text.starting_time"
                );
            }
        }
    }
    private static boolean doesServerHasNonBotOnline(List<ServerPlayerEntity> players) {
        for(ServerPlayerEntity player : players) {
            if(
                Blacklist.profileBlacklist.contains( player.getGameProfile() )
                || Blacklist.nameBlacklist.contains( player.getName().getString() )
            ) {
                continue;
            }
            return true;
        }
        return false;
    }
}
