package www.wheelershigley.me.charged;

import me.wheelershigley.charged.gamerules.WashingGameRulePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

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
