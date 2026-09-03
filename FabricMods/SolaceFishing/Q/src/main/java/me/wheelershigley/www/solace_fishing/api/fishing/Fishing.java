package me.wheelershigley.www.solace_fishing.api.fishing;

import me.wheelershigley.www.solace_fishing.api.ResultCategory;
import me.wheelershigley.www.solace_fishing.api.lore.LoreRenderedLengthComponent;
import me.wheelershigley.www.solace_fishing.api.statistics.ClimatePreferencedSampleSpace;
import me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper;
import me.wheelershigley.www.solace_fishing.registrations.FishItems;
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

import static me.wheelershigley.www.solace_fishing.api.fishing.ClimatePreference.DEFAULT_PREFERENCE;
import static me.wheelershigley.www.solace_fishing.helpers.ItemsHelper.getItemWithGlint;
import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.getFishItem;

public class Fishing {
    //TODO: replace with data-driven getter
    private static ClimatePreferencedSampleSpace treasureSampleSpace; static {
        Set<ClimatePreferencedItem> treasures = new HashSet<>();
        treasures.add(  getDefault( getItemWithGlint(Items.BOW),            1.0)  );
        treasures.add(  getDefault( getItemWithGlint(Items.ENCHANTED_BOOK), 1.0)  );
        treasures.add(  getDefault( getItemWithGlint(Items.FISHING_ROD),    1.0)  );
        treasures.add( getDefault(Items.NAME_TAG,        1.0) );
        treasures.add( getDefault(Items.NAUTILUS_SHELL,  1.0) );
        treasures.add( getDefault(Items.SADDLE,          1.0) );

        treasureSampleSpace = new ClimatePreferencedSampleSpace(treasures);
    }
    public static final ClimatePreferencedSampleSpace trashSampleSpace; static {
        Set<ClimatePreferencedItem> trashes = new HashSet<>();

        ItemStack waterBottle = PotionContents.createItemStack(
            Items.POTION,
            Potions.WATER
        );
        ItemStack inkSac = new ItemStack(Items.INK_SAC, 10);

        trashes.add( getDefault(Items.LILY_PAD,        0.17) );
        trashes.add( getDefault(Items.BONE,            0.10) );
        trashes.add( getDefault(Items.BOWL,            0.10) );
        trashes.add( getDefault(Items.LEATHER,         0.10) );
        trashes.add( getDefault(Items.LEATHER_BOOTS,   0.10) );
        trashes.add( getDefault(Items.ROTTEN_FLESH,    0.10) );
        trashes.add( getDefault(waterBottle,           0.10) );
        trashes.add( getDefault(Items.TRIPWIRE_HOOK,   0.10) );
        trashes.add( getDefault(Items.STICK,           0.05) );
        trashes.add( getDefault(Items.STRING,          0.05) );
        trashes.add( getDefault(Items.FISHING_ROD,     0.02) );
        trashes.add( getDefault(inkSac,                0.01) );

        //in Jungles, Bamboo is added to the junk loot-table
        trashes.add(
            new ClimatePreferencedItem(
                () -> { return new ItemStack(Items.BAMBOO); },
                (new ClimatePreference.Builder()).withBiomes(
                        List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE)
                ).build(),
                0.1
            )
        );

        trashSampleSpace = new ClimatePreferencedSampleSpace(trashes);
    }
    public static final ClimatePreferencedSampleSpace marineAnimalsSampleSpace; static {
        Set<ClimatePreferencedItem> marineAnimals = new HashSet<>();

        // Vanilla fishes, available everywhere; 60%:25%:13%:2%, like vanilla
        marineAnimals.add( getDefault(Items.COD,           0.60/0.60) );
        marineAnimals.add( getDefault(Items.SALMON,        0.25/0.60) );
        marineAnimals.add( getDefault(Items.PUFFERFISH,    0.13/0.60) );
        marineAnimals.add( getDefault(Items.TROPICAL_FISH, 0.02/0.60) );

        //Custom fish(es)
        marineAnimals.add( getDefault(FishItems.ALBACORE_TUNA,  1.0) );
        marineAnimals.add( getDefault(FishItems.YELLOWFIN_TUNA, 1.0) );
        marineAnimals.add( getDefault(FishItems.ANGELFISH,      1.0) );

        marineAnimalsSampleSpace = new ClimatePreferencedSampleSpace(marineAnimals);
    }

    private static ClimatePreferencedItem getDefault(Item item, @Nullable Double area) {
        return getDefault(
            new ItemStack(item),
            area
        );
    }
    private static ClimatePreferencedItem getDefault(ItemStack stack, @Nullable Double area) {
        double adjusted_area = (area == null ? 1.0 : area);

        FishItem fishItem = MetaFishingHelper.getFishItem(stack);
        if(fishItem != null) {
            ClimatePreferencedItem preferences = fishItem.preferences.clone();
            preferences.setArea( preferences.getArea() * adjusted_area );
            return preferences;
        }

        return new ClimatePreferencedItem(
            () -> stack,
            DEFAULT_PREFERENCE,
            adjusted_area
        );
    }
    //end TODO

    public static ClimatePreferencedSampleSpace getLocalSampleSpace(FishingContext context) {
        ClimatePreferencedSampleSpace localAnimals   = (ClimatePreferencedSampleSpace)marineAnimalsSampleSpace.getSortedSubSpace(context);
        ClimatePreferencedSampleSpace localTrashes   = (ClimatePreferencedSampleSpace)        trashSampleSpace.getSortedSubSpace(context);
        ClimatePreferencedSampleSpace localTreasures = (ClimatePreferencedSampleSpace)     treasureSampleSpace.getSortedSubSpace(context);

        //TODO: withTreasure in context
        Map<ResultCategory, Double> intendedWeights = context.accessories().getCategoryWeightRatios(true);
        localAnimals.normalize(   intendedWeights.get(ResultCategory.Catch   ) );
        localTrashes.normalize(   intendedWeights.get(ResultCategory.Trash   ) );
        localTreasures.normalize( intendedWeights.get(ResultCategory.Treasure) );

        // Category Joining
        Set<ClimatePreferencedItem> correctedCatches = new HashSet<>();
        correctedCatches.addAll( localAnimals.getSamples()   );
        correctedCatches.addAll( localTrashes.getSamples()   );
        correctedCatches.addAll( localTreasures.getSamples() );
        return new ClimatePreferencedSampleSpace(correctedCatches);
    }

    public static ItemStack adjustCatch(final ItemStack rawStack, FishingContext context) {
        Level level = context.environment().getLevel();
        RandomSource random = level.getRandom();

        ItemStack result = rawStack.copy();
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
                level.registryAccess(),
                Optional.empty()
            );
        }


        if( context.accessories().getHook().isEmpty() ) {
            return result;
        }
        FishItem metaDataItem = getFishItem(result);
        if(metaDataItem == null) {
            return result;
        }
        LoreRenderedLengthComponent lengthComponent = new LoreRenderedLengthComponent(
                metaDataItem.rollLength(random)
        );
        lengthComponent.set(result);
        return result;
    }

    public static ItemStack getCatch(FishingContext context, RandomSource random) {
        return adjustCatch(
            getLocalSampleSpace(context).sample(random, context),
            context
        );
    }
}
