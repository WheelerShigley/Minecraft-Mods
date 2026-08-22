package me.wheelershigley.www.window;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.wheelershigley.www.window.api.LevelArgumentType;
import me.wheelershigley.www.window.registrations.WindowBlocks;
import me.wheelershigley.www.window.registrations.WindowCommands;
import me.wheelershigley.www.window.registrations.WindowPersistentConfigurations;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class Window implements ModInitializer {
    public static final String MOD_ID = "window";
    public static Identifier getWindowIdentifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.markAsRequired();

        LevelArgumentType.register();
        WindowCommands.registerCommand();
        WindowBlocks.staticInitialize();
        WindowBlockEntities.staticInitialize();

        WindowPersistentConfigurations.registerPersistentConfig();
    }
}
