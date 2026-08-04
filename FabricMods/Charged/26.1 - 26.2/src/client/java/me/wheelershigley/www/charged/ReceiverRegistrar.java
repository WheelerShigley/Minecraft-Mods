package me.wheelershigley.www.charged;

import me.wheelershigley.www.charged.gamerules.WashingGameRulePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

public class ReceiverRegistrar {
    public static void registerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(
            WashingGameRulePayload.identifier,
            (payload, context) -> {
                context.client().execute(
                    () -> {
                        ChargedClient.isWashingEnabled = payload.value();
                    }
                );
            }
        );
    }
}
