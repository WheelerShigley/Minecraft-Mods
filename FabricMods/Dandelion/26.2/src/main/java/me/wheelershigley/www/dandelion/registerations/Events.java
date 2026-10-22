package me.wheelershigley.www.dandelion.registerations;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.wheelershigley.www.dandelion.Dandelion;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class Events {
    public static void register() {
        // First-Join Message + Starter Kit
        ServerPlayConnectionEvents.JOIN.register(
            (handler, sender, server) -> {
                ServerPlayer player = handler.player;
                boolean isFirstJoin = Dandelion.SEEN_PLAYERS.isFirstJoin( player.getUUID() );
                if(isFirstJoin) {
                    //First-Join Message
                    Component message = Component.translatable(
                        "dandelion.welcome",
                        player.getName()
                    );
                    server.getPlayerList().broadcastSystemMessage(message, false);

                    //Starter Kit
                    try {
                        player.level().getServer().getCommands().getDispatcher().execute(
                            "kit claim starter",
                            player.createCommandSourceStack()
                        );
                    } catch(CommandSyntaxException exception) {
                        throw new RuntimeException(exception);
                    }
                }
            }
        );
    }
}
