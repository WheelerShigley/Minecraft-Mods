package me.wheelershigley.www.solace_fishing.registrations;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class ItemsGroup {
    public static void initialize() {
        CreativeModeTabEvents
            .modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS)
            .register(
                group -> { group.accept(SolaceItems.ANGELFISH); }
            )
        ;

    }
}
