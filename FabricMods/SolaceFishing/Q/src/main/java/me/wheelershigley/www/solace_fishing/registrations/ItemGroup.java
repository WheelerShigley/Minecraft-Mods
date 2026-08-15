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

                    group.accept(FishingItems.MAGENTA_HOOK);
                    group.accept(FishingItems.CYAN_HOOK);
                    group.accept(FishingItems.YELLOW_LINE);
                    group.accept(FishingItems.GREEN_LINE);
                    group.accept(FishingItems.RED_BOBBER);
                    group.accept(FishingItems.BLUE_BOBBER);
                    group.accept(FishingItems.RUBBER_DUCK_BOBBER);
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
