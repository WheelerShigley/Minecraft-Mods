package me.wheelershigley.www.solace_fishing.api.fishing;

import me.wheelershigley.www.solace_fishing.api.statistics.Distribution;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class FishItem extends Item {
    private final Distribution lengthDistribution;
    private final Distribution depthPreference;
    public final ClimatePreferencedItem preferences;

    public FishItem(
        Item.Properties properties,
        @Nullable Distribution lengthDistribution,
        @Nullable Distribution depthPreference,
        ClimatePreferencedItem climatePreference
    ) {
        super(properties);

        this.lengthDistribution = lengthDistribution;
        this.depthPreference = depthPreference;
        this.preferences = climatePreference;
    }

    public @Nullable Double rollLength(RandomSource random, double multiplier) {
        if(lengthDistribution == null) {
            return null;
        }

        return lengthDistribution.getZValue( multiplier * random.nextDouble() );
    }

    public @Nullable Double getLikelihoodAtDepth(double minimum_depth, double maximum_depth) {
        if(depthPreference == null) {
            return null;
        }

        return depthPreference.getPercentile(minimum_depth, maximum_depth);
    }

    public double matchPreference(ClimateData climate) {
        if( !preferences.isInBounds(climate) ) {
            return 0.0;
        }

        return preferences.getAverageWeightAt(climate);
    }
}
