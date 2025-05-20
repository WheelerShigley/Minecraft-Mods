package me.wheelershigley.tree_in_a_forest.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sun.source.tree.Tree;
import me.wheelershigley.tree_in_a_forest.TreeInAForest;
import me.wheelershigley.tree_in_a_forest.blacklist.Blacklist;
import me.wheelershigley.tree_in_a_forest.helpers.MessagesHelper;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import static me.wheelershigley.tree_in_a_forest.helpers.ConversionsHelper.*;
import static me.wheelershigley.tree_in_a_forest.helpers.MessagesHelper.*;

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

    private static void sendBlacklistCommandMessages(
        @NotNull Boolean already,
        @NotNull Boolean success,

        @NotNull  ServerCommandSource source,
        @Nullable ServerPlayerEntity sourcePlayer,
        @NotNull  Boolean usesTellRaws,
        @NotNull  String targetPlayerName,

        @NotNull String existing_key,
        @NotNull String success_key,
        @NotNull String failure_key,
        @NotNull String console_key
    ) {
        if(already) {
            sendMessageInContext(source, sourcePlayer, existing_key, usesTellRaws, targetPlayerName);
        } else {
            if(success) {
                sendMessageInContext(source, sourcePlayer, success_key, usesTellRaws, targetPlayerName);
                sendConsoleInfoTranslatableMessage(
                    console_key,
                    sourcePlayer == null ? "<console>" : "\""+sourcePlayer.getName().getString()+"\"",
                    targetPlayerName
                );
            } else {
                sendMessageInContext(source, sourcePlayer, failure_key, usesTellRaws, targetPlayerName);
            }
        }
    }

    public static void registerCommand() {
        Command<ServerCommandSource> addBlacklistCommand = (context) -> {
            ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
            String playerName = StringArgumentType.getString(context, "target");
            GameProfile playerProfile = getProfileFromPlayerName(playerName);

            boolean success, already;
            if(playerProfile != null) {
                already = Blacklist.profileBlacklist.contains(playerProfile);
                success = Blacklist.blacklistUser(playerProfile);
                sendBlacklistCommandMessages(
                    already, success,
                    context.getSource(), sourcePlayer, false, playerName,

                    "tree_in_a_forest.text.blacklist_player_existing",
                    "tree_in_a_forest.text.blacklist_player",
                    "tree_in_a_forest.text.blacklist_player_failure",
                    "tree_in_a_forest.text.player_blacklisted_player"
                );
                TreeInAForest.updateServerTicking();
                return 0;
            }
            if( playerName != null && !playerName.isEmpty() ) {
                already = Blacklist.nameBlacklist.contains(playerName);
                success = Blacklist.blacklistUser(playerName);
                sendBlacklistCommandMessages(
                    already, success,
                    context.getSource(), sourcePlayer, false, playerName,

                    "tree_in_a_forest.text.blacklist_player_existing",
                    "tree_in_a_forest.text.blacklist_player",
                    "tree_in_a_forest.text.blacklist_player_failure",
                    "tree_in_a_forest.text.player_blacklisted_player"
                );
                TreeInAForest.updateServerTicking();
                return 0;
            }

            return 1;
        };

        Command<ServerCommandSource> removeBlacklistCommand = (context) -> {
            ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
            String playerName = StringArgumentType.getString(context, "target");
            GameProfile playerProfile = getProfileFromPlayerName(playerName);

            boolean success, already;
            if(playerProfile != null) {
                already = !Blacklist.profileBlacklist.contains(playerProfile);
                success = Blacklist.unblacklistUser(playerProfile);
                sendBlacklistCommandMessages(
                    already, success,
                    context.getSource(), sourcePlayer, false, playerName,

                    "tree_in_a_forest.text.unblacklist_player_existing",
                    "tree_in_a_forest.text.unblacklist_player",
                    "tree_in_a_forest.text.unblacklist_player_failure",
                    "tree_in_a_forest.text.player_unblacklisted_player"
                );
                TreeInAForest.updateServerTicking();
                return 0;
            }
            if( playerName != null && !playerName.isBlank() ) {
                already = !Blacklist.nameBlacklist.contains(playerName);
                success = Blacklist.unblacklistUser(playerName);
                sendBlacklistCommandMessages(
                    already, success,
                    context.getSource(), sourcePlayer, false, playerName,

                    "tree_in_a_forest.text.unblacklist_player_existing",
                    "tree_in_a_forest.text.unblacklist_player",
                    "tree_in_a_forest.text.unblacklist_player_failure",
                    "tree_in_a_forest.text.player_unblacklisted_player"
                );
                TreeInAForest.updateServerTicking();
                return 0;
            }

            return 1;
        };

        Command<ServerCommandSource> listBlacklistCommand = (context) -> {
            int blacklisted_count = Blacklist.nameBlacklist.size() + Blacklist.profileBlacklist.size();
            String namesList = getCommaSeperatedBlacklistedNames();

            String key = ""; {
                switch(blacklisted_count) {
                    case 0: {
                        key = "tree_in_a_forest.text.empty_blacklist";
                        break;
                    }
                    case 1: {
                        key = "tree_in_a_forest.text.single_blacklist";
                        break;
                    }
                    default: {
                        key = "tree_in_a_forest.text.blacklist";
                    }
                }
            }

            sendMessageInContext(
                context.getSource(),
                context.getSource().getPlayer(),
                key,
                false,
                namesList
            );
            return 0;
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
                                        "target",
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
                                        "target",
                                        StringArgumentType.string()
                                    )
                                    .suggests( new BlacklistedPlayersSuggestionProvider() )
                                    .executes(removeBlacklistCommand)
                                )
                            )
                            .then(
                                CommandManager.literal("list")
                                .executes(listBlacklistCommand)
                            )
                        )
                        .requires(isServerOrOperator)
                );
            }
        );
    }
}
