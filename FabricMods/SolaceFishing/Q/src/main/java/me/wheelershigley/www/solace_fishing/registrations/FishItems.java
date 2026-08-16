package me.wheelershigley.www.solace_fishing.registrations;

import me.wheelershigley.www.solace_fishing.data.DistributionData;
import me.wheelershigley.www.solace_fishing.implementations.DistributableItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class FishItems extends PolymerItemsRegister {
    public static final Item.Properties DEFAULT_FISH_PROPERTIES = new Item.Properties().food(
        new FoodProperties(2, 0.4f, false)
    );

    public static DistributableItem
        VANILLA_COD,
        VANILLA_SALMON,
        VANILLA_PUFFERFISH,
        VANILLA_TROPICAL_FISH
    ;

    public static DistributableItem ANGELFISH;

    public static void initialize() {
        VANILLA_COD = simpleRegister("vanilla_cod");
        VANILLA_SALMON = simpleRegister("vanilla_salmon");
        VANILLA_PUFFERFISH = simpleRegister("vanilla_pufferfish");
        VANILLA_TROPICAL_FISH = simpleRegister("vanilla_tropical_fish");

        ANGELFISH = simpleRegister("angelfish");
    }

    private static DistributableItem simpleRegister(String name) {
        return register(
            name,
            DEFAULT_FISH_PROPERTIES,
            getFishProperties(1.0, 1.0)
        );
    }

    private static Function<Item.Properties, DistributableItem> getFishProperties(
        double mean,
        double standardDeviation
    ) {
        return properties -> new DistributableItem(
            properties,
            new DistributionData(mean, standardDeviation)
        );
    }

}
