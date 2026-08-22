package me.wheelershigley.www.window.registrations;

import me.wheelershigley.www.window.WindowConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import static me.wheelershigley.www.window.Window.MOD_ID;

public class WindowPersistentConfigurations {
    public static final String FILENAME = MOD_ID + ".json";
    public static void setPersistentConfig() {
        ServerLifecycleEvents.SERVER_STARTED.register(
            server -> {
                WindowConfig.INSTANCE = WindowConfig.load(
                    server.getServerDirectory().resolve(FILENAME)
                );
            }
        );

        ServerLifecycleEvents.SERVER_STOPPING.register(
            server -> {
                WindowConfig.INSTANCE.save(
                    server.getServerDirectory().resolve(FILENAME)
                );
            }
        );
    }
}
