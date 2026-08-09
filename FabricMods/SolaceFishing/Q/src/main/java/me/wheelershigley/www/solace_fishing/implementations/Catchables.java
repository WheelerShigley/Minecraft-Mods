package me.wheelershigley.www.solace_fishing.implementations;

import me.wheelershigley.www.solace_fishing.data.ClimateData;
import me.wheelershigley.www.solace_fishing.data.ClimateStatisticItem;
import me.wheelershigley.www.solace_fishing.registrations.FishItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public class Catchables {
    public static boolean isInitialized = false;
    public static final Set<ClimateStatisticItem> statisticalCatches = new HashSet<ClimateStatisticItem>();

    public static void attemptInitialize() {
        if(isInitialized) {
            return;
        }

        Catchables.statisticalCatches.add(
            new ClimateStatisticItem(
                new ItemStack(Items.BRAIN_CORAL_FAN),
                new ClimateData(0.0, 0.0, 0.0, 0.0, 62, 0.0),
                new ClimateData(0.7, 0.7, 0.7, 0.7, 100, 0.7)
            )
        );
        Catchables.statisticalCatches.add(
            new ClimateStatisticItem(
                new ItemStack(FishItems.ANGELFISH),
                new ClimateData(0.0, 0.0, 0.0, 0.0, 62, 0.3),
                new ClimateData(0.7, 0.7, 0.7, 0.7, 100, 0.1)
            )
        );

        isInitialized = true;
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
