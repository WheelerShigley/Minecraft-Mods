package www.wheelershigley.me.trade_experience.helpers;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import www.wheelershigley.me.trade_experience.TradeExperience;

public class MessageHelper {
    public static void sendTellRaw(ServerPlayerEntity player, String translationText, Object... arguments) {
        player.sendMessage(
            Text.literal(
                Text.translatable(
                    translationText,
                    arguments
                ).getString()
            ),
            true
        );
    }

    public static void sendSentFundsTellRaw(ServerPlayerEntity sender, @Nullable ServerPlayerEntity receiver, String amount) {
        if(sender == null) {
            return;
        }

        if(receiver == null) {
            sendTellRaw(sender, "trade_experience.text.sent", amount);
        } else {
            sendTellRaw(
                sender,
                "trade_experience.text.sent_to_player",
                receiver.getName().getString(),
                amount
            );
        }
    }

    public static void sendTradeTimeOutTellRaw(ServerPlayerEntity sender, @Nullable ServerPlayerEntity receiver) {
        if(sender == null) {
            return;
        }

        if(receiver == null) {
            sendTellRaw(sender, "trade_experience.text.trade_timeout");
        } else {
            sendTellRaw(
                sender,
                "trade_experience.text.trade_timeout_to_player",
                receiver.getName().getString()
            );
        }
    }

    public static void sendReceivalTellRaw(ServerPlayerEntity receiver, @Nullable ServerPlayerEntity sender, String amount) {
        if(receiver == null) {
            return;
        }

        if(sender == null) {
            sendTellRaw(receiver, "trade_experience.text.receive", amount);
        } else {
            sendTellRaw(
                receiver,
                "trade_experience.text.received_from_player",
                amount,
                sender.getName().getString()
            );
        }
    }
}
