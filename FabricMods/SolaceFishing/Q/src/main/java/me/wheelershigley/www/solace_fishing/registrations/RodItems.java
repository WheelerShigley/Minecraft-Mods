package me.wheelershigley.www.solace_fishing.registrations;

import me.wheelershigley.www.solace_fishing.implementations.CustomFishingRod;
import net.minecraft.world.item.Item;

public class RodItems extends PolymerItemsRegister {
    public static Item BAMBOO_ROD;

    public static void initialize() {
        BAMBOO_ROD = register("bamboo_rod", CustomFishingRod::new);
    }
}
