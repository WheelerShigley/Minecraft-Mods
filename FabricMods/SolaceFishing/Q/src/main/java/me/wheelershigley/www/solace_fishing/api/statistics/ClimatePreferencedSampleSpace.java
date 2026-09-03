package me.wheelershigley.www.solace_fishing.api.statistics;

import me.wheelershigley.www.solace_fishing.api.fishing.ClimatePreferencedItem;
import me.wheelershigley.www.solace_fishing.api.fishing.FishItem;
import me.wheelershigley.www.solace_fishing.api.fishing.FishingContext;
import me.wheelershigley.www.solace_fishing.api.lore.LoreRenderedLengthComponent;
import me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.*;
import java.util.stream.Collectors;

import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.getFishItem;

public class ClimatePreferencedSampleSpace extends SampleSpace<ClimatePreferencedItem, ItemStack> {
    private boolean is_sorted = false;

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
        ClimatePreferencedSampleSpace validCatches = this.subSpace(context).withLuck(context);

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
                double adjusted_area = adjustedSample.getAverageWeightAt( context.environment() );

                FishItem animalItem = MetaFishingHelper.getFishItem( adjustedSample.getItem() );
                if(animalItem != null) {
                    Double likelihood = animalItem.getLikelihoodAtDepth( 0, context.medium_depth() );
                    if(likelihood != null) {
                        adjusted_area *= likelihood;
                    }
                }

                adjustedSample.setArea(adjusted_area);
                items.add(adjustedSample);
            }
        }
        return new ClimatePreferencedSampleSpace(items);
    }

    @Override
    public ClimatePreferencedSampleSpace getSortedSubSpace(FishingContext context) {
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

        ClimatePreferencedSampleSpace result = new ClimatePreferencedSampleSpace(sortedSamples);
        result.is_sorted = true;
        return result;
    }

    public ClimatePreferencedSampleSpace getCleaned() {
        LinkedHashSet<ClimatePreferencedItem> samples = new LinkedHashSet<>();
        for( ClimatePreferencedItem sample : this.getSamples() ) {
            if(sample.getArea() != 0.0) {
                samples.add(sample);
            }
        }
        return new ClimatePreferencedSampleSpace(samples);
    }

    public ClimatePreferencedSampleSpace withLuck(FishingContext context) {
        /* Luck is the proportion that the set is redistributed with;
         * this proportion is linearly applied across the sorted set,
         * between +luck and -luck: making less likely events more likely and more likely events less likely
         */
        LinkedHashSet<ClimatePreferencedItem> samples = ( this.is_sorted ? this : this.getSortedSubSpace(context) ).getSamples();

        LinkedHashSet<ClimatePreferencedItem> newSamples = new LinkedHashSet<>(); {
            final double MULTIPLIER = 2.0/samples.size();
            double current_multiplier;
            int index = 0;
            Iterator<ClimatePreferencedItem> iterator = samples.iterator();
            while( iterator.hasNext() ) {
                ClimatePreferencedItem item = iterator.next().clone();
                current_multiplier = MULTIPLIER*index - 1.0; //-1 to 1, linearly (by index)
                current_multiplier *= context.luck()/100.0; //-% to +%, luck
                current_multiplier += 1; // 1+(luck%)

                item.setArea(
                    current_multiplier * item.getArea()
                );
                newSamples.add(item);
                index++;
            }
        }
        return new ClimatePreferencedSampleSpace(newSamples);
    }
}
