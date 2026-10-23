package me.wheelershigley.www.dandelion.registerations;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class Creative {
    public static void registerItems() {
        CreativeModeTabEvents
            .modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register(
                (creativeTab) -> {
                    creativeTab.accept(ModItems.MENU);
                }
            )
        ;

        CreativeModeTabEvents
            .modifyOutputEvent(CreativeModeTabs.OP_BLOCKS)
            .register(
                (creativeTab) -> {
                    creativeTab.accept(ModItems.BLUE_BOOK_ICON);
                    creativeTab.accept(ModItems.DISCORD_ICON);
                    creativeTab.accept(ModItems.DANDELION_ICON);

                    creativeTab.accept(ModItems.WHITE_PORTAL_ICON);
                    creativeTab.accept(ModItems.CYAN_PORTAL_ICON);
                    creativeTab.accept(ModItems.RED_PORTAL_ICON);
                    creativeTab.accept(ModItems.PURPLE_PORTAL_ICON);
                    creativeTab.accept(ModItems.PURPLE_RESOURCE_PORTAL_ICON);
                    creativeTab.accept(ModItems.LIGHT_BLUE_PORTAL_ICON);
                    creativeTab.accept(ModItems.LIGHT_BLUE_RESOURCE_PORTAL_ICON);
                    creativeTab.accept(ModItems.END_PORTAL_ICON);
                    creativeTab.accept(ModItems.END_RESOURCE_PORTAL_ICON);

                    creativeTab.accept(ModItems.STORE_ICON);
                    creativeTab.accept(ModItems.WIKI_ICON);
                    creativeTab.accept(ModItems.NETHERITE_TEMPLATE_ICON);
                }
            )
        ;
    }
}
