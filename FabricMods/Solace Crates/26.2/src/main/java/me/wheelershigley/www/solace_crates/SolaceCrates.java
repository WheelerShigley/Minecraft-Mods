package me.wheelershigley.www.solace_crates;

import me.wheelershigley.www.solace_crates.blocks.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class SolaceCrates implements ModInitializer {
    public static final String MOD_ID = "solace_crates";

    @Override
    public void onInitialize() {
        ModBlocks.initialize();
    }

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }
}
