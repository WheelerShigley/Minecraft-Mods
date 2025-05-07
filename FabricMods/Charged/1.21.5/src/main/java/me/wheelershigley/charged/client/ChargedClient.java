package me.wheelershigley.charged.client;

import net.fabricmc.api.ClientModInitializer;

import static me.wheelershigley.charged.client.ReceiverRegistrar.registerReceiver;

public class ChargedClient implements ClientModInitializer {
    public static boolean isWashingEnabled = true;

    @Override
    public void onInitializeClient() {
        registerReceiver();
    }
}
