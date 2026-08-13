package me.wheelershigley.www.solace_fishing.registrations;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class ItemGroup {
    public static void initialize() {
        CreativeModeTabEvents
            .modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register(
                (group) -> {
                    group.accept(FishingItems.PDA);
                    group.accept(FishingItems.BAMBOO_ROD);

                    group.accept(FishingItems.A_HOOK);
                    group.accept(FishingItems.ANOTHER_HOOK);
                    group.accept(FishingItems.A_LINE);
                    group.accept(FishingItems.ANOTHER_LINE);
                    group.accept(FishingItems.RED_BOBBER);
                    group.accept(FishingItems.BLUE_BOBBER);
                }
            )
        ;
        CreativeModeTabEvents
            .modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
            .register(
                (group) -> {
                    group.accept(FishItems.ANGELFISH);
                }
            )
        ;
    }
}
