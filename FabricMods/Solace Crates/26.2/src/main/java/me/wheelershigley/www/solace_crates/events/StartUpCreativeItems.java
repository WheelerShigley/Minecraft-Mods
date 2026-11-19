package me.wheelershigley.www.solace_crates.events;

import me.wheelershigley.www.solace_crates.blocks.BlocksHelper;
import me.wheelershigley.www.solace_crates.blocks.ModBlocks;
import me.wheelershigley.www.solace_crates.crate.CrateDefinition;
import me.wheelershigley.www.solace_crates.registry.CrateRegistry;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

public class StartUpCreativeItems {
    public static void register() {
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
                                crateDefinition.MODEL()
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
