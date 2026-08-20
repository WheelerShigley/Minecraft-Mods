package me.wheelershigley.www.window;

import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import static me.wheelershigley.www.window.Window.getWindowIdentifier;

public class BlockItemIds {
    public static final ResourceKey<Block> CUSTOM_PORTAL_BLOCK = ResourceKey.create(
        Registries.BLOCK,
        getWindowIdentifier("custom_portal")
    );

    private static BlockItemId create(String path) {
        Identifier id = getWindowIdentifier(path);
        return BlockItemId.create(id, id);
    }
}
