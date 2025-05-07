package me.wheelershigley.charged.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ReceiverRegistrar {
    public static void registerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(

        );
    }
}
