package me.wheelershigley.www.solace_fishing.data;

import me.wheelershigley.www.solace_fishing.helpers.Statistics;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ClimateStatisticItem {
    private final ItemStack datum;

    private final ClimateData means;
    private final ClimateData standard_deviations;

    private boolean isWhitelisted = false;
    private List< ResourceKey<Biome> > whitelist;

    public ClimateStatisticItem(
        ItemStack datum,
        ClimateData means, ClimateData standard_deviations
    ) {
        this.datum = datum;
        this.means = means;
        this.standard_deviations = standard_deviations;
        isWhitelisted = false;
    }

    public ClimateStatisticItem(
        ItemStack datum,
        ClimateData means, ClimateData standard_deviations,
        List< ResourceKey<Biome> > whitelist
    ) {
        this.datum = datum;
        this.means = means;
        this.standard_deviations = standard_deviations;
        if( whitelist != null && !whitelist.isEmpty() ) {
            isWhitelisted = true;
            this.whitelist = whitelist;
        } else {
            isWhitelisted = false;
        }
    }

    public boolean isInBounds(
        @NotNull  ClimateData data,
        @Nullable ResourceKey<Biome> biome
    ) {
        if(
            biome != null
            && isWhitelisted
            && !whitelist.contains(biome) )
        {
            return false;
        };
        return data.isInDoubleBounds(this.means, this.standard_deviations);
    }

    public double getAverageWeightAt(ClimateData locationData) {
        double temperature_weight = Statistics.normal(
            means.temperature(),
            standard_deviations.temperature(),
            locationData.temperature()
        );
        double humidity_weight = Statistics.normal(
            means.humidity(),
            standard_deviations.humidity(),
            locationData.humidity()
        );
        double continentalness_weight = Statistics.normal(
            means.continentalness(),
            standard_deviations.continentalness(),
            locationData.continentalness()
        );
        double depth_weight = Statistics.normal(
            means.depth(),
            standard_deviations.depth(),
            locationData.depth()
        );
        double erosion_weight = Statistics.normal(
            means.erosion(),
            standard_deviations.erosion(),
            locationData.erosion()
        );
        double weirdness_weight = Statistics.normal(
            means.weirdness(),
            standard_deviations.weirdness(),
            locationData.weirdness()
        );

        double average_weight = (
            temperature_weight +
            humidity_weight +
            continentalness_weight +
            depth_weight +
            erosion_weight +
            weirdness_weight
        ) / 6.0;
        return average_weight;
    }

    public ItemStack getItem() {
        return datum.copy();
    }
}
