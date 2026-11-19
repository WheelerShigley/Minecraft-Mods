package me.wheelershigley.www.solace_crates.blocks;

import com.google.common.base.Function;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import me.wheelershigley.www.solace_crates.crate.CrateDefinition;
import me.wheelershigley.www.solace_crates.items.ItemsHelper;
import me.wheelershigley.www.solace_crates.registry.CrateRegistry;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    public static final Block CRATE = registerPolymerBlock(
        ModBlockItemIds.CRATE,
        CrateBlock::new,
        CrateBlock.PROPERTIES
    );
    public static final Block UNSET_CRATE = registerPolymerBlock(
        ModBlockItemIds.UNSET_CRATE,
        UnsetCrateBlock::new,
        CrateBlock.PROPERTIES
    );

    public static void initialize() {
        registerCreateModeTabBlocks();
    }

    private static Block registerPolymerBlock(
        BlockItemId id,
        Function<BlockBehaviour.Properties, Block> blockFactory,
        BlockBehaviour.Properties properties
    ) {
        Block block = registerPolymerBlock(id.block(), blockFactory, properties);
        BlockItem blockItem = new PolymerBlockItem(
            block,
            new Item.Properties()
                .useBlockDescriptionPrefix()
                .setId( id.item() )
        );
        Registry.register(BuiltInRegistries.ITEM, id.item(), blockItem);

        return block;
    }
    private static Block registerPolymerBlock(
        ResourceKey<Block> id,
        Function<BlockBehaviour.Properties, Block> blockFactory,
        BlockBehaviour.Properties properties
    ) {
        Block block = blockFactory.apply( properties.setId(id) );
        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    public static void registerCreateModeTabBlocks() {
        CreativeModeTabEvents
            .modifyOutputEvent(CreativeModeTabs.OP_BLOCKS)
            .register(
                (creativeTab) -> {

                    /* Unset Crate */ {
                        ItemStack crateStack = new ItemStack( ModBlocks.CRATE.asItem() );
                        crateStack.set(
                            DataComponents.ITEM_MODEL,
                            BlocksHelper.getIdentifier( ModBlocks.UNSET_CRATE )
                        );
                        crateStack.set(
                            DataComponents.CUSTOM_NAME,
                            Component.translatable(
                                ModBlocks.UNSET_CRATE.getDescriptionId()
                            ).withStyle( Style.EMPTY.withItalic(false) )
                        );
                        creativeTab.accept(crateStack);
                    }

                    // Registered Crates (only on-launch)
                    for( CrateDefinition crateDefinition : CrateRegistry.values() ) {
                        ItemStack itemStack = new ItemStack( ModBlocks.CRATE.asItem() );
                        itemStack.set(
                            DataComponents.ITEM_MODEL,
                            crateDefinition.MODEL
                        );
                        itemStack.set(
                            DataComponents.CUSTOM_NAME,
                            Component.translatable(
                                crateDefinition.getIdentifiedTranslationKey()
                            )
                        );
                        //TODO: custom data which actually sets the crate
                        creativeTab.accept(itemStack);
                    }
                }
            )
        ;
    }
}