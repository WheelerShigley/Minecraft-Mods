package me.wheelershigley.charged.helper;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MessagesHelper {
    private static final String PREFIX; static {
        StringBuilder prefixBuilder = new StringBuilder();
        prefixBuilder
            .append('<')
            .append(
                Text.literal(
                    Text.translatable("charged.text.name").getString()
                ).getString()
            )
            .append("> ")
        ;
        PREFIX = prefixBuilder.toString();
    }

    public static void sendChatMessage(ServerPlayerEntity player, String key, Object... arguments) {
        player.sendMessage(
            Text.literal(
                PREFIX + Text.translatable(key, arguments).getString()
            )
        );
    }
}
