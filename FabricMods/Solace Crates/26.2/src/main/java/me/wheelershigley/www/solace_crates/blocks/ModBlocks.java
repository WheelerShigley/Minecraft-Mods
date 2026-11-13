package me.wheelershigley.www.solace_crates.blocks;

import me.wheelershigley.www.solace_crates.SolaceCrates;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class ModBlocks {
    public static void initialize() {
        registerCreateModeTabBlocks();
    }

    public static final Block CRATE = registerBlock(
        "crate",
        (properties) -> {
            return new Block( properties.sound(SoundType.VAULT) );
        }
    );

    private static Item registerBlockItem(String name, Block block) {
        Identifier identifier = SolaceCrates.id(name);
        return Registry.register(
            BuiltInRegistries.ITEM,
            identifier,
            new BlockItem(
                block,
                new Item.Properties().useBlockDescriptionPrefix().setId(
                    ResourceKey.create(Registries.ITEM, identifier)
                )
            )
        );
    }

    private static Block registerBlock(
        String name,
        Function<BlockBehaviour.Properties, Block> function
    ) {
        Identifier identifier = SolaceCrates.id(name);
        ResourceKey<Block> resourceKey = ResourceKey.create(Registries.BLOCK, identifier);
        Block block = function.apply(
            BlockBehaviour.Properties.of().setId(resourceKey)
        );

        registerBlockItem(name, block);

        return Registry.register(
            BuiltInRegistries.BLOCK,
            identifier,
            block
        );
    }

    public static void registerCreateModeTabBlocks() {
        CreativeModeTabEvents
            .modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
            .register(
                (creativeTab) -> {
                    creativeTab.accept( ModBlocks.CRATE.asItem() );
                }
            )
        ;
    }
}