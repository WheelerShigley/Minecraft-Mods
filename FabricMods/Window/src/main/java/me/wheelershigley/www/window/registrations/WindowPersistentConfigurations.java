package me.wheelershigley.www.window.registrations;

import me.wheelershigley.www.window.WindowConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import static me.wheelershigley.www.window.Window.MOD_ID;

public class WindowPersistentConfigurations {
    public static final String CONFIG_FILE_NAME = MOD_ID + ".json";
    public static void registerPersistentConfig() {
        ServerLifecycleEvents.SERVER_STARTED.register(WindowPersistentConfigurations::load);
        ServerLifecycleEvents.SERVER_STOPPING.register(WindowPersistentConfigurations::save);
    }

    private static void load(MinecraftServer server) {
        WindowConfig.INSTANCE = WindowConfig.load(
            FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME)
        );
    }
    public static void save(MinecraftServer server) {
        WindowConfig.INSTANCE.save(
            FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME)
        );
    }
}
