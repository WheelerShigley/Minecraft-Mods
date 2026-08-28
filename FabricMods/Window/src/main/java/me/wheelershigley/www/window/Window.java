package me.wheelershigley.www.window;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.wheelershigley.www.window.registrations.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class Window implements ModInitializer {
    public static final String MOD_ID = "window";
    public static Identifier getWindowIdentifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LazyStateGen.generate();
        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.markAsRequired();

        WindowBlocks.staticInitialize();
        WindowBlockEntities.staticInitialize();
        WindowBlockEntities.registerBlockEntities();
        //WindowVirtualBlocks.register();

        WindowCommands.registerCommand();
        WindowPersistentConfigurations.registerPersistentConfig();
    }
}
