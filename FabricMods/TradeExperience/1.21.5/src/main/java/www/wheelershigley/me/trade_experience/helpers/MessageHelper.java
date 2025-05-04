package www.wheelershigley.me.trade_experience.helpers;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class MessageHelper {
    public static void sendMessage(ServerPlayerEntity player, String translationText, boolean tell_raw, Object... arguments) {
        player.sendMessage(
            Text.literal(
                Text.translatable(
                    translationText,
                    arguments
                ).getString()
            ),
            tell_raw
        );
    }

    public static void sendSentFundsChatMessage(ServerPlayerEntity sender, @Nullable ServerPlayerEntity receiver, String amount) {
        if(sender == null) {
            return;
        }

        if(receiver == null) {
            sendMessage(sender, "trade_experience.text.sent", false, amount);
        } else {
            sendMessage(
                sender,
                "trade_experience.text.sent_to_player",
                false,
                receiver.getName().getString(),
                amount
            );
        }
    }

    public static void sendTradeTimeOutChatMessage(ServerPlayerEntity sender, @Nullable ServerPlayerEntity receiver) {
        if(sender == null) {
            return;
        }

        if(receiver == null) {
            sendMessage(sender, "trade_experience.text.trade_timeout", false);
        } else {
            sendMessage(
                sender,
                "trade_experience.text.trade_timeout_to_player",
                false,
                receiver.getName().getString()
            );
        }
    }

    public static void sendReceivalChatMessage(ServerPlayerEntity receiver, @Nullable ServerPlayerEntity sender, String amount) {
        if(receiver == null) {
            return;
        }

        if(sender == null) {
            sendMessage(receiver, "trade_experience.text.receive", false, amount);
        } else {
            sendMessage(
                receiver,
                "trade_experience.text.received_from_player",
                false,
                amount,
                sender.getName().getString()
            );
        }
    }

    public static void sendInitiationTellRaw(ServerPlayerEntity target, ServerPlayerEntity initiator) {
        if(target == null) {
            return;
        }

        if(initiator == null) {
            sendMessage(target, "trade_experience.text.initiation", true);
        } else {
            sendMessage(
                target,
                "trade_experience.text.initiated_by_player",
                true,
                initiator.getName().getString()
            );
        }
    }

//    public static void sendRepetitionTellRaw(ServerPlayerEntity initiator, @Nullable ServerPlayerEntity target) {
//        if(initiator == null) {
//            return;
//        }
//
//        if(target == null) {
//            sendMessage(initiator, "trade_experience.text.repetition", true);
//        } else {
//            sendMessage(
//                initiator,
//                "trade_experience.text.repetition_with_player",
//                true,
//                target.getName().getString()
//            );
//        }
//    }
}
