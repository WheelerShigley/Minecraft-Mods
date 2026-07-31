package me.wheelershigley.www.trade_experience.helpers;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import me.wheelershigley.www.trade_experience.TradeExperience;

public class MessageHelper {
    public static void sendMessage(ServerPlayer player, String translationText, boolean tell_raw, Object... arguments) {
        player.sendSystemMessage(
            Component.literal(
                Component.translatable(
                    translationText,
                    arguments
                ).getString()
            ),
            tell_raw
        );
    }

    public static void sendSentFundsChatMessage(ServerPlayer sender, @Nullable ServerPlayer receiver, String amount) {
        if(sender == null) {
            return;
        }

        if(receiver == null) {
            sendMessage(
                sender,
                "trade_experience.text.sent",
                false,
                amount,
                TradeExperience.experienceName
            );
        } else {
            sendMessage(
                sender,
                "trade_experience.text.sent_to_player",
                false,
                receiver.getName().getString(),
                amount,
                TradeExperience.experienceName
            );
        }
    }

    public static void sendTradeTimeOutChatMessage(ServerPlayer sender, @Nullable ServerPlayer receiver) {
        if(sender == null) {
            return;
        }

        if(receiver == null) {
            sendMessage(
                sender,
                "trade_experience.text.trade_timeout",
                false,
                TradeExperience.experienceName
            );
        } else {
            sendMessage(
                sender,
                "trade_experience.text.trade_timeout_to_player",
                false,
                receiver.getName().getString()
            );
        }
    }

    public static void sendReceivalChatMessage(ServerPlayer receiver, @Nullable ServerPlayer sender, String amount) {
        if(receiver == null) {
            return;
        }

        if(sender == null) {
            sendMessage(
                receiver,
                "trade_experience.text.receive",
                false,
                amount,
                TradeExperience.experienceName
            );
        } else {
            sendMessage(
                receiver,
                "trade_experience.text.received_from_player",
                false,
                amount,
                TradeExperience.experienceName,
                sender.getName().getString()
            );
        }
    }

    public static void sendInitiationTellRaw(ServerPlayer target, ServerPlayer initiator) {
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
