package me.wheelershigley.www.window.registrations;

import me.wheelershigley.www.window.BlockItemIds;
import me.wheelershigley.www.window.portal.PortalBlock;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WindowBlocks implements ModInitializer {
    public static void staticInitialize() {}

    @Override
    public void onInitialize() {
        // Force Initializations
        WHITE_PORTAL.getClass();
        LIGHT_GRAY_PORTAL.getClass();
        GRAY_PORTAL.getClass();
        BLACK_PORTAL.getClass();
        BROWN_PORTAL.getClass();
        RED_PORTAL.getClass();
        ORANGE_PORTAL.getClass();
        YELLOW_PORTAL.getClass();
        LIME_PORTAL.getClass();
        GREEN_PORTAL.getClass();
        CYAN_PORTAL.getClass();
        LIGHT_BLUE_PORTAL.getClass();
        BLUE_PORTAL.getClass();
        PURPLE_PORTAL.getClass();
        MAGENTA_PORTAL.getClass();
        PINK_PORTAL.getClass();
    }

    public static final Block
        WHITE_PORTAL        = registerPortal(BlockItemIds.WHITE_PORTAL),
        LIGHT_GRAY_PORTAL   = registerPortal(BlockItemIds.LIGHT_GRAY_PORTAL),
        GRAY_PORTAL         = registerPortal(BlockItemIds.GRAY_PORTAL),
        BLACK_PORTAL        = registerPortal(BlockItemIds.BLACK_PORTAL),
        BROWN_PORTAL        = registerPortal(BlockItemIds.BROWN_PORTAL),
        RED_PORTAL          = registerPortal(BlockItemIds.RED_PORTAL),
        ORANGE_PORTAL       = registerPortal(BlockItemIds.ORANGE_PORTAL),
        YELLOW_PORTAL       = registerPortal(BlockItemIds.YELLOW_PORTAL),
        LIME_PORTAL         = registerPortal(BlockItemIds.LIME_PORTAL),
        GREEN_PORTAL        = registerPortal(BlockItemIds.GREEN_PORTAL),
        CYAN_PORTAL         = registerPortal(BlockItemIds.CYAN_PORTAL),
        LIGHT_BLUE_PORTAL   = registerPortal(BlockItemIds.LIGHT_BLUE_PORTAL),
        BLUE_PORTAL         = registerPortal(BlockItemIds.BLUE_PORTAL),
        PURPLE_PORTAL       = registerPortal(BlockItemIds.PURPLE_PORTAL),
        MAGENTA_PORTAL      = registerPortal(BlockItemIds.MAGENTA_PORTAL),
        PINK_PORTAL         = registerPortal(BlockItemIds.PINK_PORTAL)
    ;

    private static Block registerPortal(ResourceKey<Block> key) {
        Block block = new PortalBlock(
            BlockBehaviour.Properties.of()
            .setId(key)
            .noOcclusion()
            .noCollision()
        );
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }
}
