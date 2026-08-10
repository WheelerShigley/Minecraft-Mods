package me.wheelershigley.www.solace_fishing.implementations;

import me.wheelershigley.www.solace_fishing.data.ClimateData;
import me.wheelershigley.www.solace_fishing.data.ClimateStatisticItem;
import me.wheelershigley.www.solace_fishing.registrations.FishItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Catchables {
    public static boolean isInitialized = false;
    public static final HashMap<Item, ClimateStatisticItem> itemsCache = new HashMap<>();
    public static final Set<ClimateStatisticItem> statisticalCatches = new HashSet<>();

    private static void attemptInitialize() {
        if(isInitialized) {
            return;
        }

        // fishes
        initializeFishes();

        // rarity deviations
        for(ClimateStatisticItem item : statisticalCatches) {
            itemsCache.put(
                item.getItem().getItem(),
                item
            );
        }

        isInitialized = true;
    }

    private static void initializeFishes() {
        // Vanilla fishes, available everywhere; 60%:25%:13%:2%, like vanilla
        addDefault(Items.COD,           0.60/0.60);
        addDefault(Items.SALMON,        0.25/0.60);
        addDefault(Items.PUFFERFISH,    0.13/0.60);
        addDefault(Items.TROPICAL_FISH, 0.02/0.60);

        //Custom fishes
        Catchables.statisticalCatches.add(
            new ClimateStatisticItem(
                new ItemStack(FishItems.ANGELFISH),
                0.2,
                new ClimateData.Builder().of(ClimateData.DEFAULT_MEANS).withWeirdness(0.3).build(),
                new ClimateData.Builder().of(ClimateData.DEFAULT_DEVIATIONS).withWeirdness(0.1).build()
            )
        );
    }

    private static boolean addDefault(Item item, @Nullable Double area) {
        double adjusted_area = (area == null ? 1.0 : area);

        return Catchables.statisticalCatches.add(
            new ClimateStatisticItem(
                new ItemStack(item),
                adjusted_area,
                ClimateData.DEFAULT_MEANS.clone(),
                ClimateData.DEFAULT_DEVIATIONS.clone()
            )
        );
    }

    public static Set<ClimateStatisticItem> getValidCatchesAt(ClimateData locationData) {
        if(!isInitialized) {
            attemptInitialize();
        }
        Set<ClimateStatisticItem> validCatches = new HashSet<>();
        for(ClimateStatisticItem item : statisticalCatches) {
            if( item.isInBounds(locationData, null) ) {
                validCatches.add(item);
            }
        }
        return validCatches;
    }

    public static Map<ClimateStatisticItem, Double> getWeightsForItems(
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

    public static Map<ClimateStatisticItem, Double> normalizeWeights(
            Map<ClimateStatisticItem, Double> weights
    ) {
        double weight_sum = weights.values().stream().reduce(0.0, Double::sum);

        Map<ClimateStatisticItem, Double> correctedWeights = new HashMap<>();
        for(ClimateStatisticItem weight : weights.keySet() ) {
            correctedWeights.put(
                weight,
                weights.get(weight)/weight_sum
            );
        }
        return correctedWeights;
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
