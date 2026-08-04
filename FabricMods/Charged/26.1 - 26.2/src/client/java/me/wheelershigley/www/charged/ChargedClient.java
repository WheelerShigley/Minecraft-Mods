package me.wheelershigley.www.charged;

import net.fabricmc.api.ClientModInitializer;

import static me.wheelershigley.www.charged.ReceiverRegistrar.registerReceiver;

public class ChargedClient implements ClientModInitializer {
    public static boolean isWashingEnabled = true;

    @Override
    public void onInitializeClient() {
        registerReceiver();
    }
}
