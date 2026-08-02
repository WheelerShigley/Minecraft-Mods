package me.wheelershigley.www.tree_in_a_forest;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;

public class EventRegistrations {
    public static void registerPostServerStartUp() {
        ServerLevelEvents.LOAD.register(
            (server, world) -> {
                TreeInAForest.server = server;
            }
        );
    }
}
