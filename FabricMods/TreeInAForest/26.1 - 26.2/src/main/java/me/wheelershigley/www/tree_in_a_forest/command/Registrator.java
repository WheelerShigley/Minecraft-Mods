package me.wheelershigley.www.tree_in_a_forest.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.wheelershigley.www.tree_in_a_forest.TreeInAForest;
import me.wheelershigley.www.tree_in_a_forest.blacklist.Blacklist;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import static me.wheelershigley.www.tree_in_a_forest.helpers.ConversionsHelper.*;
import static me.wheelershigley.www.tree_in_a_forest.helpers.MessagesHelper.*;

public class Registrator {
    private static final Predicate<CommandSourceStack> isServerOrOperator = (source) -> {
        if( !source.isPlayer() ) {
            return true;
        }
        ServerPlayer sourcePlayer = source.getPlayer();
        if(sourcePlayer == null) {
            return false;
        }

        MinecraftServer server = sourcePlayer.level().getServer();
        if(server == null) {
            return false;
        }
        return server.getPlayerList().isOp(
            sourcePlayer.nameAndId()
        );
    };

    private static void sendBlacklistCommandMessages(
        @NotNull Boolean already,
        @NotNull Boolean success,

        @NotNull  CommandSourceStack source,
        @Nullable ServerPlayer sourcePlayer,
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
        Command<CommandSourceStack> addBlacklistCommand = (context) -> {
            ServerPlayer sourcePlayer = context.getSource().getPlayer();
            String playerName = StringArgumentType.getString(context, "target");
            NameAndId playerConfig = getProfileFromPlayerName(playerName);

            boolean success, already;
            if(playerConfig != null) {
                already = Blacklist.configEntryBlacklist.contains(playerConfig);
                success = Blacklist.blacklistUser(playerConfig);
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

        Command<CommandSourceStack> removeBlacklistCommand = (context) -> {
            ServerPlayer sourcePlayer = context.getSource().getPlayer();
            String playerName = StringArgumentType.getString(context, "target");
            NameAndId playerConfig = getProfileFromPlayerName(playerName);

            boolean success, already;
            if(playerConfig != null) {
                already = !Blacklist.configEntryBlacklist.contains(playerConfig);
                success = Blacklist.unblacklistUser(playerConfig);
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

        Command<CommandSourceStack> listBlacklistCommand = (context) -> {
            int blacklisted_count = Blacklist.nameBlacklist.size() + Blacklist.configEntryBlacklist.size();
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
                    Commands.literal("treeinaforest")
                        .then(
                            Commands.argument(
                                "sublet",
                                StringArgumentType.string()
                            )
                            .suggests( new BlacklistSuggestionProvider() )
                            .then(
                                Commands.literal("add")
                                .then(
                                    Commands.argument(
                                        "target",
                                        StringArgumentType.string()
                                    )
                                    .suggests( new PlayersSuggestionProvider() )
                                    .executes(addBlacklistCommand)
                                )
                            )
                            .then(
                                Commands.literal("remove")
                                .then(
                                    Commands.argument(
                                        "target",
                                        StringArgumentType.string()
                                    )
                                    .suggests( new BlacklistedPlayersSuggestionProvider() )
                                    .executes(removeBlacklistCommand)
                                )
                            )
                            .then(
                                Commands.literal("list")
                                .executes(listBlacklistCommand)
                            )
                        )
                        .requires(isServerOrOperator)
                );
            }
        );
    }
}
