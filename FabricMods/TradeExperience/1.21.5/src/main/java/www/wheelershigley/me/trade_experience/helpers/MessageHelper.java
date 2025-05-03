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

    public static void sendSentFundsTellRaw(ServerPlayerEntity sender, @Nullable String receiverName, String amount) {
        if(sender == null) {
            return;
        }
        TradeExperience.LOGGER.info(amount+"$");

        if( receiverName == null || receiverName.isBlank() ) {
            sendTellRaw(sender,"trade_experience.text.sent", amount);
        } else {
            TradeExperience.LOGGER.info("test");
            sendTellRaw(sender,"trade_experience.text.sent_to_player", receiverName, amount);
        }
    }

    public static void sendTradeTimeOutTellRaw(ServerPlayerEntity sender, @Nullable String receiverName) {
        if(sender == null) {
            return;
        }

        if( receiverName == null || receiverName.isBlank() ) {
            sendTellRaw(sender, "trade_experience.text.trade_timeout");
        } else {
            sendTellRaw(sender, "trade_experience.text.trade_timeout_to_player", receiverName);
        }
    }
}
