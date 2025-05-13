package www.wheelershigley.me.trade_experience.helpers;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.core.jmx.Server;
import www.wheelershigley.me.trade_experience.Trade;
import www.wheelershigley.me.trade_experience.TradeExperience;
import www.wheelershigley.me.trade_experience.commands.*;

import java.util.*;
import java.util.function.Predicate;

import static www.wheelershigley.me.trade_experience.TradeExperience.*;
import static www.wheelershigley.me.trade_experience.helpers.ExperienceHelper.*;
import static www.wheelershigley.me.trade_experience.helpers.MessageHelper.*;

public class Registrations {
    public static void registerPlayerClickListener() {
        UseEntityCallback.EVENT.register(
            (player, world, hand, target, hitResult) -> {
                if( !(target instanceof ServerPlayerEntity) ) {
                    return ActionResult.PASS;
                }

                if( target.isSneaking() ) {
                    return ActionResult.PASS;
                }

                UUID traderID = player.getUuid();
                Trade trade = new Trade(
                    player.getUuid(),
                    ( (PlayerEntity)target ).getUuid(),
                    world,
                    world.getTime()
                );
                boolean isNewTrade = true;
                if( activeTrades.containsKey(traderID) ) {
                    if( activeTrades.get(traderID).getReciever() != target.getUuid() ) {
                        activeTrades.replace(traderID, trade);
                    } else {
                        isNewTrade = false;
                    }
                } else {
                    activeTrades.put(traderID, trade);
                }
                if(isNewTrade) {
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

                return ActionResult.SUCCESS_SERVER;
            }
        );
    }

    private static long delta_time = 0;
    public static void registerCheckTimeoutsEachTick() {
        ServerTickEvents.END_SERVER_TICK.register(
            (server) -> {
                ArrayList<UUID> tradesToBeRemoved = new ArrayList<>();
                for( Map.Entry<UUID, Trade> activeTrade: activeTrades.entrySet() ) {
                    delta_time = activeTrade.getValue().getWorld().getTime() - activeTrade.getValue().getTime();
                    if(TradeExperience.cooldown <= delta_time) {
                        sendTradeTimeOutChatMessage(
                            server.getPlayerManager().getPlayer( activeTrade.getValue().getSender() ),
                            server.getPlayerManager().getPlayer( activeTrade.getValue().getReciever() )
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

    public static void registerCommands() {
        //balance command
        Command<ServerCommandSource> personalBalanceCommand = (context) -> {
            ServerPlayerEntity player = ( (ServerCommandSource)context.getSource() ).getPlayer();
            if(player == null) {
                return 1;
            }

            sendMessage(
                player,
                "trade_experience.command.text.balance",
                false,
                Integer.toString(
                    levelToPoints(player.experienceLevel) + getExperiencePoints(player)
                ),
                TradeExperience.experienceName
            );

            return 0;
        };

        Command<ServerCommandSource> externalBalanceCommand = (context) -> {
            ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
            if(sourcePlayer == null) {
                return 1;
            }

            Collection<GameProfile> requestedPlayers = GameProfileArgumentType.getProfileArgument(context, "target");

            ServerPlayerEntity targetPlayer;
            for(Iterator<GameProfile> profileIterator = requestedPlayers.iterator(); profileIterator.hasNext(); ) {
                GameProfile currentRequestedPlayer = profileIterator.next();
                targetPlayer = sourcePlayer.server.getPlayerManager().getPlayer( currentRequestedPlayer.getId() );
                if(targetPlayer == null) {
                    MessageHelper.sendMessage(
                        sourcePlayer,
                        "trade_experience.command.text.unknown_player",
                        false,
                        requestedPlayers
                    );
                    continue;
                }

                if(sourcePlayer.getGameProfile().getId() == targetPlayer.getGameProfile().getId() ) {
                    sendMessage(
                        sourcePlayer,
                        "trade_experience.command.text.balance",
                        false,
                        Integer.toString(
                            levelToPoints(sourcePlayer.experienceLevel) + getExperiencePoints(sourcePlayer)
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
                            levelToPoints(targetPlayer.experienceLevel) + getExperiencePoints(targetPlayer)
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
                    CommandManager
                        .literal("balance")
                        .executes(personalBalanceCommand)
                        .then(
                            CommandManager.argument(
                                "target",
                                GameProfileArgumentType.gameProfile()
                            )
                            .requires(isServerOrOperator)
                            .suggests(new PlayersSuggestionProvider() )
                            .executes(externalBalanceCommand)
                        )
                );
                dispatcher.register(
                    CommandManager
                    .literal("bal")
                    .executes(personalBalanceCommand)
                    .then(
                        CommandManager.argument(
                            "target",
                            GameProfileArgumentType.gameProfile()
                        )
                        .requires(isServerOrOperator)
                        .suggests(new PlayersSuggestionProvider() )
                        .executes(externalBalanceCommand)
                    )
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

        //mod command
        Command<ServerCommandSource> tradeExperienceCommand = (context) -> {
            String sublet = StringArgumentType.getString(context, "sublet");
            ServerPlayerEntity player = context.getSource().getPlayer();

            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder
                .append('<')
                .append(
                    Text.translatable(
                        "trade_experience.text.mod_name"
                    ).getString()
                )
                .append("> ")
            ;

            if(player != null && player.getPermissionLevel() == 0) {
                messageBuilder.append( Text.translatable("trade_experience.command.text.insufficient_permission").getString() );
            } else {
                messageBuilder.append( Text.translatable("trade_experience.command.text.reloaded").getString() );
            }

            if( sublet.equals("reload") ) {
                TradeExperience.reload();

                if(player != null) {
                    player.sendMessage(
                        Text.literal( messageBuilder.toString() )
                    );
                }

                return 0;
            }

            return 1;
        };

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> {
                    dispatcher.register(
                        CommandManager.literal(
                            MOD_ID.toLowerCase().replaceAll("_","")
                        )
                        .requires(isServerOrOperator)
                        .then(
                            CommandManager.argument(
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
