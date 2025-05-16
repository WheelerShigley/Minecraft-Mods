package me.wheelershigley.silktouchplus.helpers;

import me.wheelershigley.silktouchplus.SilkTouchPlus;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MessageHelper {
    public static void sendConsoleInfoTranslatableMessage(String key, Object... arguments) {
        SilkTouchPlus.LOGGER.info(
            Text.literal(
                Text.translatable(key, arguments).getString()
            ).getString()
        );
    }
    public static void sendPlayerTranslatableMessage(
        ServerPlayerEntity player, boolean isTellRaw,
        String key, Object... arguments
    ) {
        player.sendMessage(
            Text.literal(
                Text.translatable(key, arguments).getString()
            ),
            isTellRaw
        );
    }
}
