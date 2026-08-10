package me.wheelershigley.www.solace_fishing.implementations;

import me.wheelershigley.www.solace_fishing.data.ClimateData;
import me.wheelershigley.www.solace_fishing.data.ClimateStatisticItem;
import me.wheelershigley.www.solace_fishing.registrations.FishItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public class Catchables {
    public static boolean isInitialized = false;
    public static final Set<ClimateStatisticItem> statisticalCatches = new HashSet<>();

    public static void attemptInitialize() {
        if(isInitialized) {
            return;
        }

        //Vanilla fishes, available everywhere
        addDefault(Items.COD);
        addDefault(Items.SALMON);
        addDefault(Items.PUFFERFISH);
        addDefault(Items.TROPICAL_FISH);

        //Custom fishes
        Catchables.statisticalCatches.add(
            new ClimateStatisticItem(
                new ItemStack(FishItems.ANGELFISH),
                new ClimateData.Builder().of(ClimateData.DEFAULT_MEANS).withWeirdness(0.3).build(),
                new ClimateData.Builder().of(ClimateData.DEFAULT_DEVIATIONS).withWeirdness(0.1).build()
            )
        );

        isInitialized = true;
    }

    private static boolean addDefault(Item item) {
        return Catchables.statisticalCatches.add(
            new ClimateStatisticItem(
                new ItemStack(item),
                ClimateData.DEFAULT_MEANS,
                ClimateData.DEFAULT_DEVIATIONS
            )
        );
    }

    private static Set<ClimateStatisticItem> getValidCatchesAt(ClimateData locationData) {
        Set<ClimateStatisticItem> validCatches = new HashSet<>();
        for(ClimateStatisticItem item : statisticalCatches) {
            if( item.isInBounds(locationData, null) ) {
                validCatches.add(item);
            }
        }
        return validCatches;
    }

    private static Map<ClimateStatisticItem, Double> getWeightsForItems(
        Set<ClimateStatisticItem> items,
        ClimateData locationData
    ) {
        Map<ClimateStatisticItem, Double> weights = new HashMap<>();
        for(ClimateStatisticItem item : items) {
            weights.put(
                item,
                item.getAverageWeightAt(locationData)
            );
        }
        return weights;
    }

    public static ItemStack roll(ClimateData locationData, RandomSource random) {
        Set<ClimateStatisticItem> validItems = getValidCatchesAt(locationData);
        Map<ClimateStatisticItem, Double> weights = getWeightsForItems(validItems, locationData);

        double weight_sum = weights.values().stream().reduce(0.0, Double::sum);
        double weightcentile = random.nextDouble() * weight_sum;

        for(ClimateStatisticItem item : validItems) {
            weightcentile -= weights.get(item);
            if(weightcentile <= 0.0) {
                return item.getItem();
            }
        }

        return ItemStack.EMPTY;
    }
}
