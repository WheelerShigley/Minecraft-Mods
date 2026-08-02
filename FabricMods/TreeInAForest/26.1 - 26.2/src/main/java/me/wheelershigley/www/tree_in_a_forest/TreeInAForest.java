package me.wheelershigley.www.tree_in_a_forest;

import me.wheelershigley.www.tree_in_a_forest.blacklist.Blacklist;
import me.wheelershigley.www.tree_in_a_forest.command.Registrator;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.wheelershigley.www.tree_in_a_forest.helpers.MessagesHelper.sendConsoleInfoTranslatableMessage;

/* TODO
 * Client-Server Sync (when installed on the client-side);
 *     as of now, the skybox goes back-and-forth when everyone on is blacklisted.
 *
 * Singleplayer blacklist gets duplicated upon relog (environment set to server-only for now)
 */

public class TreeInAForest implements ModInitializer {
    public static final String MOD_ID = "tree_in_a_forest";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final HashMap<UUID, NameAndId> gameProfileCache = new HashMap<>();
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
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        TreeInAForest.serverHasOnlyBlacklistedPlayers =
            players.isEmpty() || !doesServerHasNonBotOnline(players)
        ;

        if( players.isEmpty() ) {
            tick_rate = server.tickRateManager().tickrate();
            server.tickRateManager().setTickRate(0.0f);
        } else {
            server.tickRateManager().setTickRate(tick_rate);
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
    private static boolean doesServerHasNonBotOnline(List<ServerPlayer> players) {
        for(ServerPlayer player : players) {
            if(
                Blacklist.configEntryBlacklist.contains( player.nameAndId() )
                || Blacklist.nameBlacklist.contains( player.getName().getString() )
            ) {
                continue;
            }
            return true;
        }
        return false;
    }
}
