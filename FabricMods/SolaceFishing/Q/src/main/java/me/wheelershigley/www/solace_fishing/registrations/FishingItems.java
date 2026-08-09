package me.wheelershigley.www.solace_fishing.registrations;

import me.wheelershigley.www.solace_fishing.implementations.CustomFishingRod;
import me.wheelershigley.www.solace_fishing.implementations.PDA;
import net.minecraft.world.item.Item;

public class FishingItems extends PolymerItemsRegister {
    public static Item PDA;
    public static Item BAMBOO_ROD;

    public static void initialize() {
        PDA = register(
            "pda",
            new Item.Properties().stacksTo(1),
            PDA::new
        );

        BAMBOO_ROD = register(
            "bamboo_rod",
            CustomFishingRod.DEFAULT_PROPERTIES,
            CustomFishingRod::new
        );
    }
}
