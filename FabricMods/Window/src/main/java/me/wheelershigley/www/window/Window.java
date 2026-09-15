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
     * More responces to player
     * Add scale to definition.toString (not part of definition)
     * Reverse command
     * Ignition-Block can always be placed in places that would create a portal
     *
     * Add non-creation option (portal on other side does not generate)
     * Single-Destination Portals?
     *
     * Break-Particles
     * Break-Sound
     * Portal Shatters when not defined (attempted to go through, but is not possible)
     * Gamerule for shatterings
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
