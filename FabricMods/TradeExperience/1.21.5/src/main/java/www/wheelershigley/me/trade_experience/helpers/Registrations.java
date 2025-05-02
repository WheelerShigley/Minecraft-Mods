package www.wheelershigley.me.trade_experience.helpers;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;
import www.wheelershigley.me.trade_experience.Trade;
import www.wheelershigley.me.trade_experience.TradeExperience;

import java.util.Map;
import java.util.UUID;

import static www.wheelershigley.me.trade_experience.TradeExperience.activeTrades;

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
                if( activeTrades.containsKey(traderID) ) {
                    activeTrades.replace(traderID, trade);
                } else {
                    activeTrades.put(traderID, trade);
                }

                player.sendMessage(
                    Text.literal("§aTrading with \"§e"+ target.getName().getString() +"§a\"."),
                    true
                );

                return null;
            }
        );
    }

    private static long delta_time = 0;
    private static ServerPlayerEntity sender = null;
    private static ServerPlayerEntity receiver = null;
    private static String message = "";
    public static void registerCheckTimeoutsEachTick() {
        ServerTickEvents.END_SERVER_TICK.register(
            (server) -> {
                for( Map.Entry<UUID, Trade> activeTrade: activeTrades.entrySet() ) {
                    delta_time = activeTrade.getValue().getWorld().getTime() - activeTrade.getValue().getTime();
                    if(Trade.COOLDOWN <= delta_time) {
                        activeTrades.remove( activeTrade.getKey() );

                        sender = server.getPlayerManager().getPlayer( activeTrade.getValue().getSender() );
                        if(sender != null) {
                            receiver = server.getPlayerManager().getPlayer( activeTrade.getValue().getReciever() );
                            if(receiver != null) {
                                message = "§cTrade with \"§e"+receiver.getName().getString()+"§c\" timed out.";
                            } else {
                                message = "§cTrade timed out.";
                            }
                            sender.sendMessage(
                                Text.literal(message),
                                true
                            );
                        }
                    }
                }
            }
        );
    }
}
