package me.wheelershigley.www.trade_experience.helpers;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.wheelershigley.www.trade_experience.commands.PlayersSuggestionProvider;
import me.wheelershigley.www.trade_experience.commands.ReloadSuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import me.wheelershigley.www.trade_experience.Trade;
import me.wheelershigley.www.trade_experience.TradeExperience;
import me.wheelershigley.www.trade_experience.commands.*;
import me.wheelershigley.www.trade_experience.gamerule.GameRules;

import java.util.*;
import java.util.function.Predicate;

import static me.wheelershigley.www.trade_experience.TradeExperience.*;
import static me.wheelershigley.www.trade_experience.helpers.ExperienceHelper.*;
import static me.wheelershigley.www.trade_experience.helpers.MessageHelper.*;

public class Registrations {
    public static void registerPlayerClickListener() {
        UseEntityCallback.EVENT.register(
            (player, world, hand, target, hitResult) -> {
                if( world.isClientSide() ) {
                    return InteractionResult.PASS;
                }

                boolean isEnabled; {
                    MinecraftServer server = world.getServer();
                    if(server == null) {
                        return InteractionResult.FAIL;
                    }
                    ServerLevel serverWorld = server.getLevel(
                        world.dimension()
                    );
                    if(serverWorld == null) {
                        return InteractionResult.FAIL;
                    }

                    isEnabled = serverWorld.getGameRules().get(GameRules.INTERACT_TRADE_INITIATION);
                }
                if(!isEnabled) {
                    return InteractionResult.PASS;
                }

                if( !(target instanceof ServerPlayer) ) {
                    return InteractionResult.PASS;
                }

                if( target.isShiftKeyDown() ) {
                    return InteractionResult.PASS;
                }

                UUID traderID = player.getUUID();
                Trade trade = new Trade(
                    player.getUUID(),
                    ( (Player)target ).getUUID(),
                    world,
                    world.getGameTime()
                );
                boolean isNewTrade = true;
                if( activeTrades.containsKey(traderID) ) {
                    if( activeTrades.get(traderID).getReciever() != target.getUUID() ) {
                        activeTrades.replace(traderID, trade);
                    } else {
                        isNewTrade = false;
                    }
                } else {
                    activeTrades.put(traderID, trade);
                }
                if(isNewTrade) {
                    sendInitiationTellRaw(
                        (ServerPlayer)target,
                        (ServerPlayer)player
                    );
                    sendMessage(
                        (ServerPlayer)player,
                        "trade_experience.text.trade",
                        false,
                        target.getName().getString()
                    );
                }

                return InteractionResult.SUCCESS_SERVER;
            }
        );
    }

    private static long delta_time = 0;
    public static void registerCheckTimeoutsEachTick() {
        ServerTickEvents.END_SERVER_TICK.register(
            (server) -> {
                ArrayList<UUID> tradesToBeRemoved = new ArrayList<>();
                for( Map.Entry<UUID, Trade> activeTrade : activeTrades.entrySet() ) {

                    int cooldown; {
                        MinecraftServer worldServer = activeTrade.getValue().getWorld().getServer();
                        if(worldServer == null) {
                            return;
                        }
                        ServerLevel world = worldServer.getLevel(
                            activeTrade.getValue().getWorld().dimension()
                        );
                        if (world == null) {
                            return;
                        }
                        cooldown = world.getGameRules().get(GameRules.TRADE_TIMEOUT_TIME);
                    }

                    delta_time = activeTrade.getValue().getWorld().getGameTime() - activeTrade.getValue().getTime();
                    if(cooldown <= delta_time) {
                        sendTradeTimeOutChatMessage(
                            server.getPlayerList().getPlayer( activeTrade.getValue().getSender() ),
                            server.getPlayerList().getPlayer( activeTrade.getValue().getReciever() )
                        );

                        tradesToBeRemoved.add( activeTrade.getKey() );
                    }
                }
                for(UUID uuidToRemove : tradesToBeRemoved) {
                    activeTrades.remove(uuidToRemove);
                }
            }
        );
    }

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

    public static void registerCommands() {
        //balance command
        Command<CommandSourceStack> personalBalanceCommand = (context) -> {
            ServerPlayer player = ( (CommandSourceStack)context.getSource() ).getPlayer();
            if(player == null) {
                return 1;
            }

            sendMessage(
                player,
                "trade_experience.command.text.balance",
                false,
                Integer.toString(
                    getExperiencePoints(player)
                ),
                TradeExperience.experienceName
            );

            return 0;
        };

        Command<CommandSourceStack> externalBalanceCommand = (context) -> {
            ServerPlayer sourcePlayer = context.getSource().getPlayer();
            if(sourcePlayer == null) {
                return 1;
            }

            MinecraftServer server = sourcePlayer.level().getServer();
            Collection<NameAndId> requestedPlayers = GameProfileArgument.getGameProfiles(context, "target");

            ServerPlayer targetPlayer;
            if(server == null) {
                return 0;
            }
            for(Iterator<NameAndId> configEntryIterator = requestedPlayers.iterator(); configEntryIterator.hasNext(); ) {
                NameAndId currentRequestedPlayer = configEntryIterator.next();
                targetPlayer = server.getPlayerList().getPlayer( currentRequestedPlayer.id() );
                if(targetPlayer == null) {
                    MessageHelper.sendMessage(
                        sourcePlayer,
                        "trade_experience.command.text.unknown_player",
                        false,
                        requestedPlayers
                    );
                    continue;
                }

                if(sourcePlayer.getGameProfile().id() == targetPlayer.getGameProfile().id() ) {
                    sendMessage(
                        sourcePlayer,
                        "trade_experience.command.text.balance",
                        false,
                        Integer.toString(
                            getExperiencePoints(sourcePlayer)
                        ),
                        TradeExperience.experienceName
                    );
                } else {
                    sendMessage(
                        sourcePlayer,
                        "trade_experience.command.text.external_balance",
                        false,
                        targetPlayer.getName().getString(),
                        Integer.toString(
                            getExperiencePoints(targetPlayer)
                        ),
                        TradeExperience.experienceName
                    );
                }

            }

            return 0;
        };

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                dispatcher.register(
                    Commands
                        .literal("balance")
                        .executes(personalBalanceCommand)
                        .then(
                            Commands.argument(
                                "target",
                                GameProfileArgument.gameProfile()
                            )
                            .requires(isServerOrOperator)
                            .suggests(new PlayersSuggestionProvider() )
                            .executes(externalBalanceCommand)
                        )
                );
                dispatcher.register(
                    Commands
                    .literal("bal")
                    .executes(personalBalanceCommand)
                    .then(
                        Commands.argument(
                            "target",
                            GameProfileArgument.gameProfile()
                        )
                        .requires(isServerOrOperator)
                        .suggests(new PlayersSuggestionProvider() )
                        .executes(externalBalanceCommand)
                    )
                );
            }
        );

        //payment
        Command<CommandSourceStack> paymentCommand = (context) -> {
            ServerPlayer sourcePlayer = context.getSource().getPlayer();
            if(sourcePlayer == null) {
                return 1;
            }

            MinecraftServer server = sourcePlayer.level().getServer();
            if(server == null) {
                return 1;
            }
            ServerPlayer targetPlayer = server.getPlayerList().getPlayerByName(
                StringArgumentType.getString(context, "target")
            );

            int amount = IntegerArgumentType.getInteger(context, "amount");

            Trade.performTrade(sourcePlayer, targetPlayer, amount);
            return 0;
        };

        Command<CommandSourceStack> incorrectPaymentCommand = (context) -> {
            ServerPlayer sourcePlayer = context.getSource().getPlayer();
            if(sourcePlayer == null) {
                return 1;
            }

            sourcePlayer.sendSystemMessage(
                Component.literal(
                    Component.translatable("trade_experience.command.text.unknown_amount").getString()
                )
            );
            return 0;
        };

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                dispatcher.register(
                    Commands.literal("pay")
                        .then(
                            Commands.argument(
                                "target",
                                StringArgumentType.string()
                            )
                            .suggests( new PlayersSuggestionProvider() )
                            .executes(incorrectPaymentCommand)
                            .then(
                                Commands.argument(
                                    "amount",
                                    IntegerArgumentType.integer()
                                )
                                .executes(paymentCommand)
                            )
                        )
                );
            }
        );

        //mod command
        Command<CommandSourceStack> tradeExperienceCommand = (context) -> {
            String sublet = StringArgumentType.getString(context, "sublet");
            ServerPlayer player = context.getSource().getPlayer();

            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder
                .append('<')
                .append(
                    Component.translatable(
                        "trade_experience.text.mod_name"
                    ).getString()
                )
                .append("> ")
            ;

            if(player == null) {
                return 0;
            }
            MinecraftServer server = player.level().getServer();
            if(server == null) {
                return 0;
            }
            PermissionLevel playerPermissionLevel = server.getProfilePermissions( player.nameAndId() ).level();
            if( playerPermissionLevel.isEqualOrHigherThan(PermissionLevel.MODERATORS) ) {
                messageBuilder.append(
                    Component.translatable("trade_experience.command.text.reloaded").getString()
                );
            } else {
                messageBuilder.append(
                    Component.translatable("trade_experience.command.text.insufficient_permission").getString()
                );
                return 0;
            }

            if( sublet.equals("reload") ) {
                TradeExperience.reload();

                player.sendSystemMessage(
                    Component.literal( messageBuilder.toString() )
                );

                return 0;
            }

            return 1;
        };

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> {
                    dispatcher.register(
                        Commands.literal(
                            MOD_ID.toLowerCase().replaceAll("_","")
                        )
                        .requires(isServerOrOperator)
                        .then(
                            Commands.argument(
                                "sublet",
                                StringArgumentType.string()
                            )
                            .suggests( new ReloadSuggestionProvider() )
                            .executes(tradeExperienceCommand)
                        )
                    );
                }
        );
    }
}
