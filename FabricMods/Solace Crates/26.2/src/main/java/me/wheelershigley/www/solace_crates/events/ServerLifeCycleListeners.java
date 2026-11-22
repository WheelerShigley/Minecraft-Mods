package me.wheelershigley.www.solace_crates.events;

import me.wheelershigley.www.solace_crates.configurations.CrateDefinitionsCodec;
import me.wheelershigley.www.solace_crates.configurations.CrateLootTablesLocation;
import me.wheelershigley.www.solace_crates.configurations.PlayersHistoryCodec;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class ServerLifeCycleListeners {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(
            (server) -> {
                CrateLootTablesLocation.INSTANCE.initialize(server);
                CrateDefinitionsCodec.INSTANCE.initialize(server);

                save(server);
            }
        );

        ServerLifecycleEvents.AFTER_SAVE.register(ServerLifeCycleListeners::save);
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerLifeCycleListeners::save);

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(
            (server, resourceManager, success) -> {
                if(success) {
                    CrateLootTablesLocation.INSTANCE.reload(server);
                    CrateDefinitionsCodec.reload();
                    PlayersHistoryCodec.reload();
                }
            }
        );
    }

    private static void save(MinecraftServer server, boolean flush, boolean force) {
        save(server);
    }
    private static void save(MinecraftServer server) {
        CrateDefinitionsCodec.INSTANCE.save();
        PlayersHistoryCodec.INSTANCE.save();
    }
}
