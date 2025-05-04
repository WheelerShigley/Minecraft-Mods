package www.wheelershigley.me.trade_experience.helpers;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import www.wheelershigley.me.trade_experience.Trade;
import www.wheelershigley.me.trade_experience.commands.*;

import java.util.Map;
import java.util.UUID;

import static www.wheelershigley.me.trade_experience.TradeExperience.activeTrades;
import static www.wheelershigley.me.trade_experience.helpers.ExperienceHelper.*;
import static www.wheelershigley.me.trade_experience.helpers.MessageHelper.*;

public class Registrations {
    public static void registerPlayerClickListener() {
        UseEntityCallback.EVENT.register(
            (player, world, hand, target, hitResult) -> {
                if( !(target instanceof ServerPlayerEntity) ) {
                    return null;
                }

                UUID traderID = player.getUuid();
                Trade trade = new Trade(
                    player.getUuid(),
                    ( (PlayerEntity)target ).getUuid(),
                    world,
                    world.getTime()
                );
//                boolean isNewTrade = false;
                if( activeTrades.containsKey(traderID) ) {
//                    if( activeTrades.get(traderID).getReciever() == target.getUuid() ) {
//                        sendRepetitionTellRaw(
//                            (ServerPlayerEntity)player,
//                            (ServerPlayerEntity)target
//                        );
//                    } else {
//                        activeTrades.replace(traderID, trade);
//                        isNewTrade = true;
//                    }
                    if( activeTrades.get(traderID).getReciever() != target.getUuid() ) {
                        activeTrades.replace(traderID, trade);
                    }
                } else {
                    activeTrades.put(traderID, trade);
//                    isNewTrade = true;
//                }
//                if(isNewTrade) {
                    sendInitiationTellRaw(
                        (ServerPlayerEntity)target,
                        (ServerPlayerEntity)player
                    );
                    sendMessage(
                        (ServerPlayerEntity)player,
                        "trade_experience.text.trade",
                        false,
                        target.getName().getString()
                    );
                }

                return null;
            }
        );
    }

    private static long delta_time = 0;
    public static void registerCheckTimeoutsEachTick() {
        ServerTickEvents.END_SERVER_TICK.register(
            (server) -> {
                for( Map.Entry<UUID, Trade> activeTrade: activeTrades.entrySet() ) {
                    delta_time = activeTrade.getValue().getWorld().getTime() - activeTrade.getValue().getTime();
                    if(Trade.COOLDOWN <= delta_time) {
                        sendTradeTimeOutChatMessage(
                            server.getPlayerManager().getPlayer( activeTrade.getValue().getSender() ),
                            server.getPlayerManager().getPlayer( activeTrade.getValue().getReciever() )
                        );

                        activeTrades.remove( activeTrade.getKey() );
                    }
                }
            }
        );
    }

    public static void registerCommands() {
        //balance command
        Command<ServerCommandSource> balanceCommand = (context) -> {
            ServerPlayerEntity player = ( (ServerCommandSource)context.getSource() ).getPlayer();
            if(player == null) {
                return 1;
            }

            sendMessage(
                player,
                "trade_experience.text.balance",
                false,
                Integer.toString(
                    levelToPoints(player.experienceLevel) + getExperiencePoints(player)
                )
            );

            return 0;
        };

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                dispatcher.register(
                    CommandManager.literal("balance").executes(balanceCommand)
                );
                dispatcher.register(
                    CommandManager.literal("bal").executes(balanceCommand)
                );
            }
        );

        //payment
        Command<ServerCommandSource> paymentCommand = (context) -> {
            ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
            if(sourcePlayer == null) {
                return 1;
            }

            ServerPlayerEntity targetPlayer = sourcePlayer.server.getPlayerManager().getPlayer(
                StringArgumentType.getString(context, "target")
            );

            int amount = IntegerArgumentType.getInteger(context, "amount");

            Trade.performTrade(sourcePlayer, targetPlayer, amount);
            return 0;
        };

        Command<ServerCommandSource> incorrectPaymentCommand = (context) -> {
            ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
            if(sourcePlayer == null) {
                return 1;
            }

            sourcePlayer.sendMessage(
                Text.literal(
                    Text.translatable("trade_experience.command.text.unknown_amount").getString()
                )
            );
            return 0;
        };

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                dispatcher.register(
                    CommandManager.literal("pay")
                        .then(
                            CommandManager.argument(
                                "target",
                                StringArgumentType.string()
                            )
                            .suggests( new PlayersSuggestionProvider() )
                            .executes(incorrectPaymentCommand)
                            .then(
                                CommandManager.argument(
                                    "amount",
                                    IntegerArgumentType.integer()
                                )
                                .executes(paymentCommand)
                            )
                        )
                );
            }
        );
    }
}
