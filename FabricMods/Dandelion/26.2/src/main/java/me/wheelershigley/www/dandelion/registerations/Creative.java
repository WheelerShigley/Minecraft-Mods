package me.wheelershigley.www.dandelion.registerations;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class Creative {
    public static void registerItems() {
        CreativeModeTabEvents
            .modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register(
                (creativeTab) -> creativeTab.accept(ModItems.MENU)
            )
        ;
    }
}
