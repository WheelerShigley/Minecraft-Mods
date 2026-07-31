package me.wheelershigley.www.trade_experience.mixin;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.wheelershigley.www.trade_experience.Trade;
import me.wheelershigley.www.trade_experience.gamerule.GameRules;

import java.util.UUID;

import static me.wheelershigley.www.trade_experience.TradeExperience.activeTrades;

@Mixin(PlayerList.class)
public class SendMessageCheckMixin {
    @Shadow @Final private MinecraftServer server;

    @Inject(
        method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
        at = @At("HEAD"),
         cancellable = true
    )
    public void broadcast(PlayerChatMessage message, ServerPlayer sender, ChatType.Bound params, CallbackInfo ci) {
        InteractionResult result = attemptTrade(message, sender);
        if( !result.equals(InteractionResult.PASS) ) {
            ci.cancel();
        }
    }

    @Unique
    private InteractionResult attemptTrade(PlayerChatMessage message, ServerPlayer sender) {
        UUID senderID = sender.getUUID();
        if( !activeTrades.containsKey(senderID) ) {
            return InteractionResult.PASS;
        }

        //remove old trades
        long delta_time = sender.level().getGameTime() - activeTrades.get(senderID).getTime();
        int cooldown = sender.level().getGameRules().get(GameRules.TRADE_TIMEOUT_TIME);
        if(cooldown < delta_time) {
            activeTrades.remove(senderID);
            return InteractionResult.PASS;
        }

        String messageContent = message.signedContent();

        boolean isInteger = messageContent.matches("[0-9]+");
        if(!isInteger) {
            return InteractionResult.PASS;
        }

        ServerPlayer receiver = server.getPlayerList().getPlayer( activeTrades.get(senderID).getReciever() );

        int amount = Integer.parseInt(messageContent);
        //amount may not be negative; this would non-consentually withdraw from others
        if(amount <= 0) {
            return InteractionResult.FAIL;
        }

        Trade.performTrade(sender, receiver, amount);
        activeTrades.remove( sender.getUUID() );
        return InteractionResult.SUCCESS_SERVER;
    }
}
