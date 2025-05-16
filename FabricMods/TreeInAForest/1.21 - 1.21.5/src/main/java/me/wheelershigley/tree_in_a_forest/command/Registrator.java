package me.wheelershigley.tree_in_a_forest.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.wheelershigley.tree_in_a_forest.blacklist.Blacklist;
import me.wheelershigley.tree_in_a_forest.TreeInAForest;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;
import java.util.function.Predicate;

import static me.wheelershigley.tree_in_a_forest.blacklist.ConversionsHelper.*;

public class Registrator {
    private static final Predicate<ServerCommandSource> isServerOrOperator = (source) -> {
        if( !source.isExecutedByPlayer() ) {
            return true;
        }
        ServerPlayerEntity sourcePlayer = source.getPlayer();
        if(sourcePlayer == null) {
            return false;
        }

        return sourcePlayer.server.getPlayerManager().isOperator(
            sourcePlayer.getGameProfile()
        );
    };

    public static void registerCommand() {
        Command<ServerCommandSource> addBlacklistCommand = (context) -> {
            String playerName = StringArgumentType.getString(context, "player");
            GameProfile playerProfile = getProfileFromPlayerName(playerName);

            if(playerProfile != null) {
                Blacklist.blacklistUser(playerProfile);
                //TODO: message player
                return 0;
            }
            if( playerName != null && !playerName.isEmpty() ) {
                Blacklist.blacklistUser(playerName);
                //TODO: message player
                return 0;
            }

            //TODO: message player
            return 1;
        };

        Command<ServerCommandSource> removeBlacklistCommand = (context) -> {
            String playerName = StringArgumentType.getString(context, "player");
            GameProfile playerProfile = getProfileFromPlayerName(playerName);

            if(playerProfile != null) {
                Blacklist.unblacklistUser(playerProfile);
                return 0;
            }
            if( playerName != null && !playerName.isBlank() ) {
                Blacklist.unblacklistUser(playerName);
                return 0;
            }

            return 1;
        };

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                dispatcher.register(
                    CommandManager.literal("treeinaforest")
                        .then(
                            CommandManager.argument(
                                "sublet",
                                StringArgumentType.string()
                            )
                            .suggests( new BlacklistSuggestionProvider() )
                            .then(
                                CommandManager.literal("add")
                                .then(
                                    CommandManager.argument(
                                        "player",
                                        StringArgumentType.string()
                                    )
                                    .suggests( new PlayersSuggestionProvider() )
                                    .executes(addBlacklistCommand)
                                )
                            )
                            .then(
                                CommandManager.literal("remove")
                                .then(
                                    CommandManager.argument(
                                        "player",
                                        StringArgumentType.string()
                                    )
                                    .suggests( new BlacklistedPlayersSuggestionProvider() )
                                    .executes(removeBlacklistCommand)
                                )
                            )
                        )
                        .requires(isServerOrOperator)
                );
            }
        );
    }
}
