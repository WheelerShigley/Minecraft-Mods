package www.wheelershigley.me.charged;

import net.fabricmc.api.ClientModInitializer;

import static www.wheelershigley.me.charged.ReceiverRegistrar.registerReceiver;

public class ChargedClient implements ClientModInitializer {
    public static boolean isWashingEnabled = true;

    @Override
    public void onInitializeClient() {
        registerReceiver();
    }
}
