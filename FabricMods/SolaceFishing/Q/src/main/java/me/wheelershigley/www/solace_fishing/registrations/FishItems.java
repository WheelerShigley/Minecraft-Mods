package me.wheelershigley.www.solace_fishing.registrations;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class FishItems extends PolymerItemsRegister {
    private static final FoodProperties rawFishNutrition = new FoodProperties(2, 1, false);

    public static Item ANGELFISH;

    public static void initialize() {
        ANGELFISH = register( "angelfish",  new Item.Properties().food(rawFishNutrition) );
    }

}
