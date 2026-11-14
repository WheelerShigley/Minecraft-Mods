package me.wheelershigley.www.solace_crates.events;

import me.wheelershigley.www.solace_crates.configurations.CrateLootTablesLocation;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ServerLifeCycleListeners {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(CrateLootTablesLocation.INSTANCE::initialize);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(
            (server, resourceManager, success) -> {
                if(success) {
                    CrateLootTablesLocation.INSTANCE.reload(server);
                }
            }
        );
    }
}
