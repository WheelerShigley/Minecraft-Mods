package me.wheelershigley.www.solace_crates.events;

import me.wheelershigley.www.solace_crates.configurations.CrateDefinitionsCodec;
import me.wheelershigley.www.solace_crates.configurations.CrateLootTablesLocation;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ServerLifeCycleListeners {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(
            (server) -> {
                CrateLootTablesLocation.INSTANCE.initialize(server);
                CrateDefinitionsCodec.INSTANCE.initialize(server);

                CrateDefinitionsCodec.INSTANCE.save();
            }
        );

        ServerLifecycleEvents.AFTER_SAVE.register(
            (server, flush, force) -> {
                CrateDefinitionsCodec.INSTANCE.save();
            }
        );
        ServerLifecycleEvents.SERVER_STOPPING.register(
            (server) -> {
                CrateDefinitionsCodec.INSTANCE.save();
            }
        );

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(
            (server, resourceManager, success) -> {
                if(success) {
                    CrateDefinitionsCodec.INSTANCE.initialize(server);
                    CrateLootTablesLocation.INSTANCE.reload(server);
                }
            }
        );
    }
}
