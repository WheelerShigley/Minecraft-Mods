package me.wheelershigley.www.solace_crates;

import me.wheelershigley.www.solace_crates.blocks.ModBlockEntities;
import me.wheelershigley.www.solace_crates.blocks.ModBlocks;
import me.wheelershigley.www.solace_crates.registry.CrateRegistry;
import me.wheelershigley.www.solace_crates.events.ServerLifeCycleListeners;
import me.wheelershigley.www.solace_crates.items.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SolaceCrates implements ModInitializer {
    public static final String MOD_ID = "solace_crates";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CrateRegistry.initialize();

        ModItems.initialize();
        ModBlockEntities.initialize();
        ModBlocks.initialize();

        ServerLifeCycleListeners.register();

        //TODO: set-crate command
        // /solace_crates set_crate X Y Z definition_identifier

        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.markAsRequired();
    }

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }
}
