package me.wheelershigley.www.solace_fishing.registrations;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class ItemGroup {
    public static void initialize() {
        CreativeModeTabEvents
            .modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
            .register(
                group -> {
                    group.accept(FishItems.ANGELFISH);
                }
            )
        ;
        CreativeModeTabEvents
            .modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register(
                group -> {
                    group.accept(FishingItems.PDA);
                    group.accept(FishingItems.BAMBOO_ROD);
                }
            )
        ;
    }
}
