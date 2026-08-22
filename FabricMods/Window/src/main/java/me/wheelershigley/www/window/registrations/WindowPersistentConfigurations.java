package me.wheelershigley.www.window.registrations;

import me.wheelershigley.www.window.WindowConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

import static me.wheelershigley.www.window.Window.MOD_ID;

public class WindowPersistentConfigurations {
    public static final String FILENAME = MOD_ID + ".json";
    public static void registerPersistentConfig() {
        ServerLifecycleEvents.SERVER_STARTED.register(
            server -> load()
        );

        ServerLifecycleEvents.SERVER_STOPPING.register(
            server -> save()
        );
    }

    private static void load() {
        WindowConfig.INSTANCE = WindowConfig.load(
            FabricLoader.getInstance().getConfigDir().resolve(FILENAME)
        );
    }
    public static void save() {
        WindowConfig.INSTANCE.save(
            FabricLoader.getInstance().getConfigDir().resolve(FILENAME)
        );
    }
}
