package me.wheelershigley.charged.client;

import me.wheelershigley.charged.Charged;
import me.wheelershigley.charged.gamerules.WashingGameRulePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ReceiverRegistrar {
    public static void registerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(
            WashingGameRulePayload.identifier,
            (payload, context) -> {
                context.client().execute(
                    () -> {
                        Charged.LOGGER.info("b = "+ payload.value() +";");
                        ChargedClient.isWashingEnabled = payload.value();
                    }
                );
            }
        );
    }
}
