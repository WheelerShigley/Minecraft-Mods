package me.wheelershigley.diegetic;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MessageHelper {
    public static void sendMessage(ServerPlayerEntity player, String message) {
        if( message.isEmpty() || message.isBlank() ) {
            return;
        }

        player.sendMessage( Text.literal(message), true );
    }
}
