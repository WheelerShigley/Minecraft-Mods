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
     * Ignition-Block can always be placed in places that would create a portal
     * Ignition-Block in internal-area of portal still ignites it
     * There can be gaps in the portal-frame, this should be fixed
     * Add warning for duplicates in definition
     * Add "add" commandlet for links [command]
     *
     * Add non-creation option (portal on other side does not generate)
     * RTP portals
     * Single-Destination Portals?
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
