package me.wheelershigley.www.solace_fishing.implementations;

import me.wheelershigley.www.solace_fishing.api.*;
import me.wheelershigley.www.solace_fishing.api.fishing.ClimateData;
import me.wheelershigley.www.solace_fishing.api.fishing.ClimatePreference;
import me.wheelershigley.www.solace_fishing.api.fishing.ClimatePreferencedItem;
import me.wheelershigley.www.solace_fishing.api.fishing.FishingContext;
import me.wheelershigley.www.solace_fishing.api.lore.LoreRenderedLengthComponent;
import me.wheelershigley.www.solace_fishing.api.statistics.NormalDistribution;
import me.wheelershigley.www.solace_fishing.registrations.FishItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.wheelershigley.www.solace_fishing.api.fishing.ClimatePreference.DEFAULT_PREFERENCE;
import static me.wheelershigley.www.solace_fishing.helpers.ItemsHelper.*;
import static me.wheelershigley.www.solace_fishing.helpers.ItemsHelper.conditionallyWithTranslatedLore;
import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.getDistributedItem;

public class Catchables {
    public static boolean isInitialized = false;
    public static final HashMap<Item, ClimatePreferencedItem> itemsCache = new HashMap<>();

    //TODO: replace population with dynamically-gotten loot-tables
    public static final Set<ClimatePreferencedItem> statisticalCatches = new HashSet<>();
    public static final Set<ClimatePreferencedItem> statisticalTrashes; static {
        statisticalTrashes = new HashSet<>();

        ItemStack waterBottle = PotionContents.createItemStack(
            Items.POTION,
            Potions.WATER
        );
        ItemStack inkSac = new ItemStack(Items.INK_SAC, 10);

        statisticalTrashes.add( getDefault(Items.LILY_PAD,        0.17) );
        statisticalTrashes.add( getDefault(Items.BONE,            0.10) );
        statisticalTrashes.add( getDefault(Items.BOWL,            0.10) );
        statisticalTrashes.add( getDefault(Items.LEATHER,         0.10) );
        statisticalTrashes.add( getDefault(Items.LEATHER_BOOTS,   0.10) );
        statisticalTrashes.add( getDefault(Items.ROTTEN_FLESH,    0.10) );
        statisticalTrashes.add( getDefault(waterBottle,           0.10) );
        statisticalTrashes.add( getDefault(Items.TRIPWIRE_HOOK,   0.10) );
        statisticalTrashes.add( getDefault(Items.STICK,           0.05) );
        statisticalTrashes.add( getDefault(Items.STRING,          0.05) );
        statisticalTrashes.add( getDefault(Items.FISHING_ROD,     0.02) );
        statisticalTrashes.add( getDefault(inkSac,                0.01) );

        //in Jungles, Bamboo is added to the junk loot-table
        statisticalTrashes.add(
            new ClimatePreferencedItem(
                new ItemStack(Items.BAMBOO),
                (new ClimatePreference.Builder()).withBiomes(
                    List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE)
                ).build(),
                0.1
            )
        );
    }
    public static final Set<ClimatePreferencedItem> statisticalTreasures; static {
        statisticalTreasures = new HashSet<>();

        statisticalTreasures.add(  getDefault( getItemWithGlint(Items.BOW),            1.0) );
        statisticalTreasures.add(  getDefault( getItemWithGlint(Items.ENCHANTED_BOOK), 1.0) );
        statisticalTreasures.add(  getDefault( getItemWithGlint(Items.FISHING_ROD),    1.0) );
        statisticalTreasures.add( getDefault(Items.NAME_TAG,        1.0) );
        statisticalTreasures.add( getDefault(Items.NAUTILUS_SHELL,  1.0) );
        statisticalTreasures.add( getDefault(Items.SADDLE,          1.0) );
    }

    private static void attemptInitialize() {
        if(isInitialized) {
            return;
        }

        // fishes
        initializeFishes();

        // rarity deviations
        for(ClimatePreferencedItem item : statisticalCatches) {
            itemsCache.put(
                item.getItem().getItem(),
                item
            );
        }

        isInitialized = true;
    }

    private static void initializeFishes() {
        // Vanilla fishes, available everywhere; 60%:25%:13%:2%, like vanilla
        Catchables.statisticalCatches.add( getDefault(Items.COD,           0.60/0.60) );
        Catchables.statisticalCatches.add( getDefault(Items.SALMON,        0.25/0.60) );
        Catchables.statisticalCatches.add( getDefault(Items.PUFFERFISH,    0.13/0.60) );
        Catchables.statisticalCatches.add( getDefault(Items.TROPICAL_FISH, 0.02/0.60) );

        //Custom fish(es)
        Catchables.statisticalCatches.add( getDefault(FishItems.YELLOWFIN_TUNA, 1.0) );
        Catchables.statisticalCatches.add(
            new ClimatePreferencedItem(
                new ItemStack(FishItems.ANGELFISH),
                (new ClimatePreference.Builder()).withWeirdnessPreference(
                    new NormalDistribution(0.3, 0.25)
                ).build(),
                1.0
            )
        );
    }

    private static ClimatePreferencedItem getDefault(Item item, @Nullable Double area) {
        return getDefault(
            new ItemStack(item),
            area
        );
    }
    private static ClimatePreferencedItem getDefault(ItemStack item, @Nullable Double area) {
        double adjusted_area = (area == null ? 1.0 : area);
        return new ClimatePreferencedItem(item, DEFAULT_PREFERENCE, adjusted_area);
    }

    public static Set<ClimatePreferencedItem> getWeightedValidCatches(
        FishingContext context,
        boolean withTreasure,
        boolean withLore
    ) {
        if(!isInitialized) {
            attemptInitialize();
        }

        // Validation-pass
        Set<ClimatePreferencedItem> validFishes = getValidSubCatchesAt(
            statisticalCatches, context, withLore, null
        );
        Set<ClimatePreferencedItem> validTrashes = getValidSubCatchesAt(
            statisticalTrashes, context, withLore, "solace_fishing.category.trash"
        );
        Set<ClimatePreferencedItem> validTreasures = getValidSubCatchesAt(
            withTreasure ? statisticalTreasures : Set.of(),
            context,
            withLore,
            "solace_fishing.category.treasure"
        );

        // Weights
        double fishes_sum_weight = 0.0;
        for(ClimatePreferencedItem item : validFishes) {
            fishes_sum_weight += item.getAverageWeightAt( context.environment() );
        }
        double trashes_sum_weight = 0.0;
        for(ClimatePreferencedItem item : validTrashes) {
            trashes_sum_weight += item.getAverageWeightAt( context.environment() );
        }
        double treasures_sum_weight = 0.0;
        for(ClimatePreferencedItem item : validTreasures) {
            treasures_sum_weight += item.getAverageWeightAt( context.environment() );
        }

        Map<ResultCategory, Double> intendedWeights = context.accessories().getCategoryWeightRatios(withTreasure);
        double weights_sum = fishes_sum_weight + trashes_sum_weight + treasures_sum_weight;

        enforceAreaRatio( intendedWeights.get(ResultCategory.Catch),    weights_sum, fishes_sum_weight,    validFishes    );
        enforceAreaRatio( intendedWeights.get(ResultCategory.Trash),    weights_sum, trashes_sum_weight,   validTrashes   );
        enforceAreaRatio( intendedWeights.get(ResultCategory.Treasure), weights_sum, treasures_sum_weight, validTreasures );

        // Category Joining
        Set<ClimatePreferencedItem> correctedCatches = new HashSet<>();
        correctedCatches.addAll(validFishes);
        correctedCatches.addAll(validTrashes);
        correctedCatches.addAll(validTreasures);


        return correctedCatches;
    }

    private static Set<ClimatePreferencedItem> getValidSubCatchesAt(
        Set<ClimatePreferencedItem> potentialItems,
        FishingContext context,
        boolean withLore,
        @Nullable String translation_key
    ) {
        Set<ClimatePreferencedItem> validSubCatches = new HashSet<>();
        for(ClimatePreferencedItem item : potentialItems) {
            ClimatePreferencedItem copy = item.clone();
            if(  copy.isInBounds( context.environment() )  ) {
                if(translation_key != null) {
                    copy.setItem(
                        conditionallyWithTranslatedLore(withLore, item.getItem(), translation_key)
                    );
                }
                validSubCatches.add(copy);
            }
        }
        return validSubCatches;
    }

    private static void enforceAreaRatio(
        double enforced_sum_total, double sum_total, double category_sum_total,
        Set<ClimatePreferencedItem> itemsInCategory
    ) {
        double enforcement_ratio = (enforced_sum_total * sum_total)/category_sum_total;
        for(ClimatePreferencedItem item : itemsInCategory) {
            item.setArea( enforcement_ratio * item.getArea() );
        }
    }

    public static Map<ClimatePreferencedItem, Double> getWeightsForItems(
        Set<ClimatePreferencedItem> items,
        ClimateData locationData
    ) {
        Map<ClimatePreferencedItem, Double> weights = new HashMap<>();
        for(ClimatePreferencedItem item : items) {
            weights.put(
                item,
                item.getAverageWeightAt(locationData)
            );
        }
        return weights;
    }

    public static Map<ClimatePreferencedItem, Double> normalizeWeights(
            Map<ClimatePreferencedItem, Double> weights
    ) {
        double weight_sum = weights.values().stream().reduce(0.0, Double::sum);

        Map<ClimatePreferencedItem, Double> correctedWeights = new HashMap<>();
        for(ClimatePreferencedItem weight : weights.keySet() ) {
            correctedWeights.put(
                weight,
                weights.get(weight)/weight_sum
            );
        }
        return correctedWeights;
    }

    public static ItemStack roll(
        FishingContext context,
        boolean withTreasure,
        RandomSource random, RegistryAccess access
    ) {
        ItemStack result = ItemStack.EMPTY;

        Set<ClimatePreferencedItem> validItems = getWeightedValidCatches(context, withTreasure, false);
        Map<ClimatePreferencedItem, Double> weights = getWeightsForItems(validItems, context.environment());

        double weight_sum = weights.values().stream().reduce(0.0, Double::sum);
        double weightcentile = random.nextDouble() * weight_sum;

        for(ClimatePreferencedItem item : validItems) {
            weightcentile -= weights.get(item);
            if(weightcentile <= 0.0) {
                result = item.getItem();
                break;
            }
        }

        // modify tools/armor
        if( result.isDamageableItem() ) {
            result.setDamageValue(
                random.nextInt( 1, result.getMaxDamage() )
            );
        }
        if( result.has(DataComponents.ENCHANTMENT_GLINT_OVERRIDE) ) {
            result.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);

            result = EnchantmentHelper.enchantItem(
                random,
                result.getItem() == Items.ENCHANTED_BOOK ? new ItemStack(Items.BOOK) : result,
                30,
                access,
                Optional.empty()
            );
        }


        if( context.accessories().getHook().isEmpty() ) {
            return result;
        }

        DistributableItem lengthedItem = getDistributedItem(result);
        if(lengthedItem == null) {
            return result;
        }
        LoreRenderedLengthComponent lengthComponent = new LoreRenderedLengthComponent(
            lengthedItem.getDistributionResult(random)
        );
        lengthComponent.set(result);

        return result;
    }
}
