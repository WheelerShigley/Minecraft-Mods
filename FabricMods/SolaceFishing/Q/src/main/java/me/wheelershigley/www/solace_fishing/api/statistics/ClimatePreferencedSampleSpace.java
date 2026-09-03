package me.wheelershigley.www.solace_fishing.api.statistics;

import me.wheelershigley.www.solace_fishing.api.fishing.ClimatePreferencedItem;
import me.wheelershigley.www.solace_fishing.api.fishing.FishItem;
import me.wheelershigley.www.solace_fishing.api.fishing.FishingContext;
import me.wheelershigley.www.solace_fishing.api.lore.LoreRenderedLengthComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.*;
import java.util.stream.Collectors;

import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.getFishItem;

public class ClimatePreferencedSampleSpace extends SampleSpace<ClimatePreferencedItem, ItemStack> {
    public ClimatePreferencedSampleSpace() {}
    public ClimatePreferencedSampleSpace(Set<ClimatePreferencedItem> items) {
        super(items);
        normalize();
    }

    @Override
    public void normalize() {
        double sum_probability = 0.0;
        for( ClimatePreferencedItem sample : this.getSamples() ) {
            sum_probability += sample.getArea();
        }
        for( ClimatePreferencedItem sample : this.getSamples() ) {
            sample.setArea( sample.getArea() / sum_probability);
        }
    }
    public void normalize(double forced_area) {
        double sum_probability = 0.0;
        for( ClimatePreferencedItem sample : this.getSamples() ) {
            sum_probability += sample.getArea();
        }

        double probability_ratio = forced_area / sum_probability;
        for( ClimatePreferencedItem sample : this.getSamples() ) {
            sample.setArea( sample.getArea() * probability_ratio );
        }
    }

    @Override
    public ItemStack sample(RandomSource random, FishingContext context) {
        ClimatePreferencedSampleSpace validCatches = this.subSpace(context);

        ItemStack result = ItemStack.EMPTY;
        double percentile = random.nextDouble();
        for( ClimatePreferencedItem sample : validCatches.getSamples() ) {
            percentile -= sample.getArea();
            if(percentile <= 0.0) {
                result = sample.getItem();
                break;
            }
        }

        //TODO: simplify
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
                context.environment().getLevel().registryAccess(),
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

    private ClimatePreferencedSampleSpace subSpace(FishingContext context) {
        Set<ClimatePreferencedItem> items = new HashSet<>();
        for( ClimatePreferencedItem sample : this.getSamples() ) {
            if(  sample.isInBounds( context.environment() )  ) {
                ClimatePreferencedItem adjustedSample = sample.clone();
                adjustedSample.setArea(
                    adjustedSample.getAverageWeightAt( context.environment() )
                );

                items.add(adjustedSample);
            }
        }
        return new ClimatePreferencedSampleSpace(items);
    }

    @Override
    public SampleSpace<ClimatePreferencedItem, ItemStack> getSortedSubSpace(FishingContext context) {
        ClimatePreferencedSampleSpace validCatches = this.subSpace(context);

        HashSet<ClimatePreferencedItem> sortedSamples = validCatches.getSamples()
            .stream()
            .sorted(
                Comparator
                    .comparingDouble(ClimatePreferencedItem::getArea)
                    .reversed()
            )
            .collect(
                Collectors.toCollection(LinkedHashSet::new)
            )
        ;

        return new ClimatePreferencedSampleSpace(sortedSamples);
    }
}
