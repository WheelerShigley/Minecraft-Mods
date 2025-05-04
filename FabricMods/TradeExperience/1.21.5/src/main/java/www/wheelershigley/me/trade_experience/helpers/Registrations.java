package www.wheelershigley.me.trade_experience.helpers;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import www.wheelershigley.me.trade_experience.Trade;

import java.util.Map;
import java.util.UUID;

import static www.wheelershigley.me.trade_experience.TradeExperience.activeTrades;
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
                }
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
//                }

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
}
