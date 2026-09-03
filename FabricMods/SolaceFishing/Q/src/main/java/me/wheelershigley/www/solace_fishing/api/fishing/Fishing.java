package me.wheelershigley.www.solace_fishing.api.fishing;

import me.wheelershigley.www.solace_fishing.api.ResultCategory;
import me.wheelershigley.www.solace_fishing.api.lore.LoreRenderedLengthComponent;
import me.wheelershigley.www.solace_fishing.api.statistics.ClimatePreferencedSampleSpace;
import me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper;
import me.wheelershigley.www.solace_fishing.registrations.FishItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.FishingHook;
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
    private static final ClimatePreferencedSampleSpace treasureSampleSpace; static {
        Set<ClimatePreferencedItem> treasures = new HashSet<>();
        treasures.add(  getDefault( getItemWithGlint(Items.BOW),            null)  );
        treasures.add(  getDefault( getItemWithGlint(Items.ENCHANTED_BOOK), null)  );
        treasures.add(  getDefault( getItemWithGlint(Items.FISHING_ROD),    null)  );
        treasures.add( getDefault(Items.NAME_TAG,       null) );
        treasures.add( getDefault(Items.NAUTILUS_SHELL, null) );
        treasures.add( getDefault(Items.SADDLE,         null) );

        treasureSampleSpace = new ClimatePreferencedSampleSpace(treasures);
    }
    private static final ClimatePreferencedSampleSpace trashSampleSpace; static {
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
    private static final ClimatePreferencedSampleSpace marineAnimalsSampleSpace; static {
        Set<ClimatePreferencedItem> marineAnimals = new HashSet<>();

        // Vanilla fish(es)
        marineAnimals.add( getDefault(Items.COD,            null) );
        marineAnimals.add( getDefault(Items.SALMON,         null) );
        marineAnimals.add( getDefault(Items.PUFFERFISH,     null) );
        marineAnimals.add( getDefault(Items.TROPICAL_FISH,  null) );

        //Custom fish(es)
        marineAnimals.add( getDefault(FishItems.ALBACORE_TUNA,  null) );
        marineAnimals.add( getDefault(FishItems.YELLOWFIN_TUNA, null) );
        marineAnimals.add( getDefault(FishItems.ANGELFISH,      null) );

        marineAnimalsSampleSpace = new ClimatePreferencedSampleSpace(marineAnimals);
    }

    private static ClimatePreferencedItem getDefault(Item item, @Nullable Double area) {
        return getDefault(
            new ItemStack(item),
            area == null ? 1.0 : area
        );
    }
    private static ClimatePreferencedItem getDefault(ItemStack stack, @Nullable Double area) {
        FishItem fishItem = MetaFishingHelper.getFishItem(stack);
        if(fishItem != null) {
            return fishItem.preferences.clone();
        }

        return new ClimatePreferencedItem(
            () -> stack,
            DEFAULT_PREFERENCE,
            area == null ? 1.0 : area
        );
    }
    //end TODO

    public static ClimatePreferencedSampleSpace getLocalSampleSpace(FishingContext context) {
        ClimatePreferencedSampleSpace localAnimals   = marineAnimalsSampleSpace.getSortedSubSpace(context);
        ClimatePreferencedSampleSpace localTrashes   =         trashSampleSpace.getSortedSubSpace(context);
        ClimatePreferencedSampleSpace localTreasures =      treasureSampleSpace.getSortedSubSpace(context);

        Map<ResultCategory, Double> intendedWeights = context.accessories().getCategoryWeightRatios( context.isOpenWater() );
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
