package me.wheelershigley.www.window.registrations;

import me.wheelershigley.www.window.BlockItemIds;
import me.wheelershigley.www.window.portal.PortalBlock;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class WindowBlocks implements ModInitializer {
    public static void staticInitialize() {}

    @Override
    public void onInitialize() {
        // Force Initializations
        PORTAL.getClass();
    }

    public static final Block PORTAL = register(
        BlockItemIds.PORTAL,
        PortalBlock::new,
        BlockBehaviour.Properties.of().noOcclusion().noCollision()
    );

    private static Block register(
        ResourceKey<Block> id,
        Function<BlockBehaviour.Properties, Block> blockFactory,
        BlockBehaviour.Properties properties
    ) {
        Block block = blockFactory.apply( properties.setId(id) );
        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }
}
