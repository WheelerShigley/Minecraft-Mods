package me.wheelershigley.diegetic.helper;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class MessageHelper {
    public static void sendMessage(ServerPlayerEntity player, @NotNull String key, Object... arguments) {
        player.sendMessage(
            Text.literal(
                Text.translatable(
                    key,
                    arguments
                ).getString()
            ),
            true
        );
    }
}
