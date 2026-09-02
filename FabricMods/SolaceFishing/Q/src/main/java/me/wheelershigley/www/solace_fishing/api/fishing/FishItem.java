package me.wheelershigley.www.solace_fishing.api.fishing;

import me.wheelershigley.www.solace_fishing.api.statistics.Distribution;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class FishItem extends Item {
    private final Distribution lengthDistribution;
    public final ClimatePreferencedItem preferences;

    public FishItem(
        Item.Properties properties,
        @Nullable Distribution lengthDistribution,
        ClimatePreferencedItem climatePreference
    ) {
        super(properties);

        this.lengthDistribution = lengthDistribution;
        this.preferences = climatePreference;
    }

    public @Nullable Double rollLength(RandomSource random) {
        if(lengthDistribution == null) {
            return null;
        }

        return lengthDistribution.get( random.nextDouble() );
    }

    public double matchPreference(ClimateData climate) {
        if( !preferences.isInBounds(climate) ) {
            return 0.0;
        }

        return preferences.getAverageWeightAt(climate);
    }
}
