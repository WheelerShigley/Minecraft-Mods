package me.wheelershigley.www.solace_crates.blocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class BlocksHelper {
    public static Identifier getIdentifier(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }
    public static @Nullable Block getBlock(Identifier identifier) {
        return BuiltInRegistries.BLOCK.getOptional(identifier).orElse(null);
    }

}
