package www.wheelershigley.me.trade_experience.mixin;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import www.wheelershigley.me.trade_experience.Trade;

import java.util.UUID;

import static www.wheelershigley.me.trade_experience.TradeExperience.activeTrades;
import static www.wheelershigley.me.trade_experience.helpers.MessageHelper.*;

@Mixin(PlayerManager.class)
public class SendMessageCheckMixin {
    @Shadow @Final private MinecraftServer server;

    @Inject(
        method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V",
        at = @At("HEAD"),
         cancellable = true
    )
    public void broadcast(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params, CallbackInfo ci) {
        ActionResult result = attemptTrade(message, sender);
        if( !result.equals(ActionResult.PASS) ) {
            ci.cancel();
        }
    }

    @Unique
    private ActionResult attemptTrade(SignedMessage message, ServerPlayerEntity sender) {
        UUID senderID = sender.getUuid();
        if( !activeTrades.containsKey(senderID) ) {
            return ActionResult.PASS;
        }

        //remove old trades
        long delta_time = sender.getWorld().getTime() - activeTrades.get(senderID).getTime();
        if(Trade.COOLDOWN < delta_time) {
            activeTrades.remove(senderID);
            return ActionResult.PASS;
        }

        String messageContent = message.getSignedContent();

        boolean isInteger = messageContent.matches("[0-9]+");
        if(!isInteger) {
            return ActionResult.PASS;
        }

        ServerPlayerEntity receiver = sender.server.getPlayerManager().getPlayer( activeTrades.get(senderID).getReciever() );

        int amount = Integer.parseInt(messageContent);
        ActionResult tradeResult = activeTrades.get(senderID).execute(sender.server, amount);
        if(tradeResult == null || tradeResult != ActionResult.SUCCESS_SERVER) {
            sendTellRaw(sender, "trade_experience.text.send_failure");
            return ActionResult.FAIL;
        }

        sendSentFundsTellRaw(sender, receiver, messageContent);
        sendReceivalTellRaw( receiver, sender, messageContent);
        activeTrades.remove( sender.getUuid() );

        return ActionResult.SUCCESS_SERVER;
    }
}
