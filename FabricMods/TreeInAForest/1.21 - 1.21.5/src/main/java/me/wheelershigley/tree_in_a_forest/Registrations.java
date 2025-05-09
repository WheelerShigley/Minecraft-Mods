package me.wheelershigley.tree_in_a_forest;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class Registrations {
    public static void registerPostServerStartUp() {
        ServerWorldEvents.LOAD.register(
            (server, world) -> {
                TreeInAForest.server = server;
            }
        );
    }
}
