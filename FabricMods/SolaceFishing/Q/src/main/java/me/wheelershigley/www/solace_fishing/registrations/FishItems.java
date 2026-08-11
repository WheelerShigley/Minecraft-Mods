package me.wheelershigley.www.solace_fishing.registrations;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class FishItems extends PolymerItemsRegister {
    private static final Item.Properties DEFAULT_FISH_PROPERTIES = new Item.Properties().food(
        new FoodProperties(2, 0.4f, false)
    );

    public static Item ANGELFISH;

    public static void initialize() {
        ANGELFISH = register("angelfish", DEFAULT_FISH_PROPERTIES);
    }

}
