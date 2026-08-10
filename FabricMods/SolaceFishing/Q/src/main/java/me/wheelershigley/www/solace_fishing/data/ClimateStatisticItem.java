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
            means.getTemperature(),
            standard_deviations.getTemperature(),
            locationData.getTemperature()
        );
        double humidity_weight = Statistics.normal(
            means.getHumidity(),
            standard_deviations.getHumidity(),
            locationData.getHumidity()
        );
        double continentalness_weight = Statistics.normal(
            means.getContinentalness(),
            standard_deviations.getContinentalness(),
            locationData.getContinentalness()
        );
        double depth_weight = Statistics.normal(
            means.getDepth(),
            standard_deviations.getDepth(),
            locationData.getDepth()
        );
        double erosion_weight = Statistics.normal(
            means.getErosion(),
            standard_deviations.getErosion(),
            locationData.getErosion()
        );
        double weirdness_weight = Statistics.normal(
            means.getWeirdness(),
            standard_deviations.getWeirdness(),
            locationData.getWeirdness()
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

    public double getAverageStandardDeviation() {
        return (
            standard_deviations.getTemperature() +
            standard_deviations.getHumidity() +
            standard_deviations.getContinentalness() +
            standard_deviations.getDepth() +
            standard_deviations.getErosion() +
            standard_deviations.getWeirdness()
        ) / 6.0;
    }

    public ItemStack getItem() {
        return datum.copy();
    }
}
