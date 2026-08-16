package me.wheelershigley.www.solace_fishing.implementations;

import me.wheelershigley.www.solace_fishing.data.*;
import me.wheelershigley.www.solace_fishing.registrations.FishItems;
import me.wheelershigley.www.solace_fishing.registrations.FishingItems;
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

import static me.wheelershigley.www.solace_fishing.helpers.ItemsHelper.*;
import static me.wheelershigley.www.solace_fishing.helpers.ItemsHelper.conditionallyWithTranslatedLore;
import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.getDistributedItem;

public class Catchables {
    public static boolean isInitialized = false;
    public static final HashMap<Item, ClimateStatisticItem> itemsCache = new HashMap<>();

    //TODO: replace population with dynamically-gotten loot-tables
    public static final Set<ClimateStatisticItem> statisticalCatches = new HashSet<>();
    public static final Set<ClimateStatisticItem> statisticalTrashes; static {
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
            new ClimateStatisticItem(
                new ItemStack(Items.BAMBOO),
                0.1,
                ClimateData.DEFAULT_MEANS.clone(),
                ClimateData.DEFAULT_DEVIATIONS.clone(),
                List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE),
                null
            )
        );
    }
    public static final Set<ClimateStatisticItem> statisticalTreasures; static {
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
        Catchables.statisticalCatches.add( getDefault(Items.COD,           0.60/0.60) );
        Catchables.statisticalCatches.add( getDefault(Items.SALMON,        0.25/0.60) );
        Catchables.statisticalCatches.add( getDefault(Items.PUFFERFISH,    0.13/0.60) );
        Catchables.statisticalCatches.add( getDefault(Items.TROPICAL_FISH, 0.02/0.60) );

        //Custom fish(es)
        Catchables.statisticalCatches.add(
            new ClimateStatisticItem(
                new ItemStack(FishItems.ANGELFISH),
                1.0,
                new ClimateData.Builder().of(ClimateData.DEFAULT_MEANS).withWeirdness(0.3).build(),
                new ClimateData.Builder().of(ClimateData.DEFAULT_DEVIATIONS).withWeirdness(0.1).build()
            )
        );
    }

    private static ClimateStatisticItem getDefault(Item item, @Nullable Double area) {
        return getDefault(
            new ItemStack(item),
            area
        );
    }
    private static ClimateStatisticItem getDefault(ItemStack item, @Nullable Double area) {
        double adjusted_area = (area == null ? 1.0 : area);

        return new ClimateStatisticItem(
            item,
            adjusted_area,
            ClimateData.DEFAULT_MEANS.clone(),
            ClimateData.DEFAULT_DEVIATIONS.clone()
        );
    }

    public static Set<ClimateStatisticItem> getWeightedValidCatches(
        FishingContext context,
        boolean withTreasure,
        boolean withLore
    ) {
        if(!isInitialized) {
            attemptInitialize();
        }

        // Validation-pass
        Set<ClimateStatisticItem> validFishes = getValidSubCatchesAt(
            statisticalCatches, context, withLore, null
        );
        Set<ClimateStatisticItem> validTrashes = getValidSubCatchesAt(
            statisticalTrashes, context, withLore, "solace_fishing.trash"
        );
        Set<ClimateStatisticItem> validTreasures = getValidSubCatchesAt(
            withTreasure ? statisticalTreasures : Set.of(),
            context,
            withLore,
            "solace_fishing.treasure"
        );

        // Weights
        double fishes_sum_weight = 0.0;
        for(ClimateStatisticItem item : validFishes) {
            fishes_sum_weight += item.getAverageWeightAt( context.environment() );
        }
        double trashes_sum_weight = 0.0;
        for(ClimateStatisticItem item : validTrashes) {
            trashes_sum_weight += item.getAverageWeightAt( context.environment() );
        }
        double treasures_sum_weight = 0.0;
        for(ClimateStatisticItem item : validTreasures) {
            treasures_sum_weight += item.getAverageWeightAt( context.environment() );
        }

        Map<ResultCategory, Double> intendedWeights = getCategoryWeightsForContext(context, withTreasure);
        double weights_sum = fishes_sum_weight + trashes_sum_weight + treasures_sum_weight;

        enforceAreaRatio( intendedWeights.get(ResultCategory.Catch),    weights_sum, fishes_sum_weight,    validFishes    );
        enforceAreaRatio( intendedWeights.get(ResultCategory.Trash),    weights_sum, trashes_sum_weight,   validTrashes   );
        enforceAreaRatio( intendedWeights.get(ResultCategory.Treasure), weights_sum, treasures_sum_weight, validTreasures );

        // Category Joining
        Set<ClimateStatisticItem> correctedCatches = new HashSet<>();
        correctedCatches.addAll(validFishes);
        correctedCatches.addAll(validTrashes);
        correctedCatches.addAll(validTreasures);


        return correctedCatches;
    }

    //TODO: integrate enchantment(s)
    /* By Default, the sum-weight of treasures should comprise 5%
    of the total weights; similarly, trashes will comprise 10% of the total weights.
    */
    private static Map<ResultCategory, Double> getCategoryWeightsForContext(
        FishingContext context, boolean includeTreasure
    ) {
        double treasure_weight = (includeTreasure ? 0.05 : 0.00);
        double catch_weight    = 0.9 - treasure_weight;
        double trash_weight    = 0.1;

        // Double Trash-changes when using a Rubber-Duck-Bobber
        ItemStack bobber = context.accessories().getBobber();
        if( !bobber.isEmpty() && bobber.getItem() == FishingItems.RUBBER_DUCK_BOBBER ) {
            double sum_nontrash_weight = treasure_weight + catch_weight;

            trash_weight    *= 2.0;
            treasure_weight *= (1.0 - trash_weight) / sum_nontrash_weight;
            catch_weight    *= (1.0 - trash_weight) / sum_nontrash_weight;
        }


        Map<ResultCategory, Double> intendedWeights = new HashMap<>();
        intendedWeights.put(ResultCategory.Catch,    catch_weight   );
        intendedWeights.put(ResultCategory.Trash,    trash_weight   );
        intendedWeights.put(ResultCategory.Treasure, treasure_weight);

        return intendedWeights;
    }

    private static Set<ClimateStatisticItem> getValidSubCatchesAt(
        Set<ClimateStatisticItem> potentialItems,
        FishingContext context,
        boolean withLore,
        @Nullable String translation_key
    ) {
        Set<ClimateStatisticItem> validSubCatches = new HashSet<>();
        for(ClimateStatisticItem item : potentialItems) {
            ClimateStatisticItem copy = item.clone();
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
        Set<ClimateStatisticItem> itemsInCategory
    ) {
        double enforcement_ratio = (enforced_sum_total * sum_total)/category_sum_total;
        for(ClimateStatisticItem item : itemsInCategory) {
            item.setArea( enforcement_ratio * item.getArea() );
        }
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

    public static ItemStack roll(
        FishingContext context,
        boolean withTreasure,
        RandomSource random, RegistryAccess access
    ) {
        ItemStack result = ItemStack.EMPTY;

        Set<ClimateStatisticItem> validItems = getWeightedValidCatches(context, withTreasure, false);
        Map<ClimateStatisticItem, Double> weights = getWeightsForItems(validItems, context.environment());

        double weight_sum = weights.values().stream().reduce(0.0, Double::sum);
        double weightcentile = random.nextDouble() * weight_sum;

        for(ClimateStatisticItem item : validItems) {
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
                result,
                30,
                access,
                Optional.empty()
            );
        }


        DistributableItem lengthedItem = getDistributedItem(result);
        if(lengthedItem == null) {
            return result;
        }
        DistributionData lengthData = lengthedItem.getDistributionData();
        if(lengthData == null) {
            return result;
        }

        System.out.println(
            // TODO
            lengthData.roll(random)
        );

        return result;
    }
}
