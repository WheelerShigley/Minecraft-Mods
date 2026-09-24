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

    /* TODO
     * LevelHelper::getSafeRTPLocation
     *
     * Break-Particles
     * Break-Sound
     *
     * Portal Shatters when not defined (attempted to go through, but is not possible)
     * Gamerule for shatterings
     * Single-Destination Portals?
     * Ignition-Block can always be placed in places that would create a portal
     */
    @Override
    public void onInitialize() {
        LazyStateGen.generate();
        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.markAsRequired();

        WindowBlocks.staticInitialize();
        WindowBlockEntities.staticInitialize();
        WindowBlockEntities.registerBlockEntities();

        WindowCommands.registerCommand();
        WindowPersistentConfigurations.registerPersistentConfig();
    }
}
