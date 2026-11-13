package me.wheelershigley.www.solace_crates;

import me.wheelershigley.www.solace_crates.blocks.ModBlockEntities;
import me.wheelershigley.www.solace_crates.blocks.ModBlocks;
import me.wheelershigley.www.solace_crates.items.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;

public class SolaceCrates implements ModInitializer {
    public static final String MOD_ID = "solace_crates";

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModBlockEntities.initialize();
        ModBlocks.initialize();

        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.markAsRequired();
    }

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }
}
