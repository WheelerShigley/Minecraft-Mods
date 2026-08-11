package me.wheelershigley.www.solace_fishing.implementations;

import me.wheelershigley.www.solace_fishing.data.ClimateData;
import me.wheelershigley.www.solace_fishing.data.ClimateStatisticItem;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.wheelershigley.www.solace_fishing.helpers.ItemsHelper.*;

public class Catchables {
    public static boolean isInitialized = false;
    public static final HashMap<Item, ClimateStatisticItem> itemsCache = new HashMap<>();
    public static final Set<ClimateStatisticItem> statisticalCatches = new HashSet<>();

    public static final Map<ClimateStatisticItem, Double> trashWeights; static {
        trashWeights = new HashMap<>();

        ItemStack waterBottle = PotionContents.createItemStack(
            Items.POTION,
            Potions.WATER
        );
        ItemStack inkSac = new ItemStack(Items.INK_SAC, 10);

        trashWeights.put( getDefault(Items.LILY_PAD,        1.0), 0.17 );
        trashWeights.put( getDefault(Items.BONE,            1.0), 0.10 );
        trashWeights.put( getDefault(Items.BOWL,            1.0), 0.10 );
        trashWeights.put( getDefault(Items.LEATHER,         1.0), 0.10 );
        trashWeights.put( getDefault(Items.LEATHER_BOOTS,   1.0), 0.10 );
        trashWeights.put( getDefault(Items.ROTTEN_FLESH,    1.0), 0.10 );
        trashWeights.put( getDefault(waterBottle,           1.0), 0.10 );
        trashWeights.put( getDefault(Items.TRIPWIRE_HOOK,   1.0), 0.10 );
        trashWeights.put( getDefault(Items.STICK,           1.0), 0.05 );
        trashWeights.put( getDefault(Items.STRING,          1.0), 0.05 );
        trashWeights.put( getDefault(Items.FISHING_ROD,     1.0), 0.02 );
        trashWeights.put( getDefault(inkSac,                1.0), 0.01 );

        //in Jungles, Bamboo is added to the junk loot-table
        trashWeights.put(
            new ClimateStatisticItem(
                new ItemStack(Items.BAMBOO),
                1.0,
                ClimateData.DEFAULT_MEANS.clone(),
                ClimateData.DEFAULT_DEVIATIONS.clone(),
                List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE),
                null
            ),
            0.10
        );
    }
    public static final Map<ClimateStatisticItem, Double> treasureWeights; static {
        treasureWeights = new HashMap<>();

        treasureWeights.put(  getDefault( getItemWithGlint(Items.BOW),            1.0), 1.0  );
        treasureWeights.put(  getDefault( getItemWithGlint(Items.ENCHANTED_BOOK), 1.0), 1.0  );
        treasureWeights.put(  getDefault( getItemWithGlint(Items.FISHING_ROD),    1.0), 1.0  );
        treasureWeights.put( getDefault(Items.NAME_TAG,        1.0), 1.0);
        treasureWeights.put( getDefault(Items.NAUTILUS_SHELL,  1.0), 1.0);
        treasureWeights.put( getDefault(Items.SADDLE,          1.0), 1.0);
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
                0.2,
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

    public static Set<ClimateStatisticItem> getValidCatchesAt(
        ClimateData locationData,
        boolean withTreasure,
        boolean withLore
    ) {
        if(!isInitialized) {
            attemptInitialize();
        }

        System.out.println( Boolean.toString(withLore) );

        //fish
        double catch_weight_sum = 0.0;
        Set<ClimateStatisticItem> validCatches = new HashSet<>();
        for(ClimateStatisticItem item : statisticalCatches) {
            ClimateStatisticItem copy = item.clone();
            if( copy.isInBounds(locationData) ) {
                validCatches.add(copy);
                catch_weight_sum += copy.getAverageWeightAt(locationData);
            }
        }

        //trash
        double trash_weight_sum = 0.0;
        Set<ClimateStatisticItem> validTrashes = new HashSet<>();
        for(ClimateStatisticItem item : trashWeights.keySet() ) {
            ClimateStatisticItem copy = item.clone();
            if( copy.isInBounds(locationData) ) {
                if(withLore) {
                    copy.setItem(
                        stackWithTranslatedLore( item.getItem(), "solace_fishing.trash")
                    );
                }

                validTrashes.add(copy);
                trash_weight_sum += copy.getAverageWeightAt(locationData);
            }
        }

        //treasure
        double treasure_weight_sum = 0.0;
        Set<ClimateStatisticItem> validTreasures = new HashSet<>();
        if(withTreasure) {
            for(ClimateStatisticItem item : treasureWeights.keySet() ) {
                ClimateStatisticItem copy = item.clone();
                if( copy.isInBounds(locationData) ) {
                    if(withLore) {
                        copy.setItem(
                            stackWithTranslatedLore( item.getItem(), "solace_fishing.treasure")
                        );
                    }

                    validTreasures.add(copy);
                    treasure_weight_sum += copy.getAverageWeightAt(locationData);
                }
            }
        }

        /* The sum-weight of treasures should comprise 5%
        of the total weights; similarly, trashes will comprise 10% of the total weights.
        */
        Set<ClimateStatisticItem> correctedCatches = new HashSet<>();
        double sum_weight_sum = catch_weight_sum + trash_weight_sum + treasure_weight_sum;
        double enforcement_ratio;

        enforcement_ratio = 0.90 - (withTreasure ? 0.05 : 0.00);
        enforcement_ratio = (enforcement_ratio * sum_weight_sum)/catch_weight_sum;
        for(ClimateStatisticItem item : validCatches) {
            item.setArea( enforcement_ratio * item.getArea() );
            correctedCatches.add(item);
        }

        enforcement_ratio = (0.10 * sum_weight_sum)/trash_weight_sum;
        for(ClimateStatisticItem item : validTrashes) {
            item.setArea( enforcement_ratio * item.getArea() );
            correctedCatches.add(item);
        }

        if(withTreasure) {
            enforcement_ratio = (0.05 * sum_weight_sum)/treasure_weight_sum;
            for(ClimateStatisticItem item : validTreasures) {
                item.setArea( enforcement_ratio * item.getArea() );
                correctedCatches.add(item);
            }
        }

        return correctedCatches;
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

    //TODO: integrate luck
    public static ItemStack roll(
        ClimateData locationData,
        float luck, boolean withTreasure,
        RandomSource random, RegistryAccess access
    ) {
        ItemStack result = ItemStack.EMPTY;

        Set<ClimateStatisticItem> validItems = getValidCatchesAt(locationData, withTreasure, false);
        Map<ClimateStatisticItem, Double> weights = getWeightsForItems(validItems, locationData);

        double weight_sum = weights.values().stream().reduce(0.0, Double::sum);
        double weightcentile = random.nextDouble() * weight_sum;

        for(ClimateStatisticItem item : validItems) {
            weightcentile -= weights.get(item);
            if(weightcentile <= 0.0) {
                result = item.getItem();
                break;
            }
        }

        // damage and enchant tools/armor
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

        return result;
    }
}
