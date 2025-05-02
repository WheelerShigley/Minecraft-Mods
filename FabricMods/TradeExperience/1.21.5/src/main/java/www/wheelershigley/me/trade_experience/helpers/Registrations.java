package www.wheelershigley.me.trade_experience.helpers;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import www.wheelershigley.me.trade_experience.Trade;

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
                    Text.literal("Trading with \""+ target.getName().getString() +"\"."),
                    true
                );

                return null;
            }
        );
    }
}
