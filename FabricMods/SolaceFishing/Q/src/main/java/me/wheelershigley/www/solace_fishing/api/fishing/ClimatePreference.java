package me.wheelershigley.www.solace_fishing.api.fishing;

import me.wheelershigley.www.solace_fishing.api.statistics.Distribution;
import me.wheelershigley.www.solace_fishing.api.statistics.NormalDistribution;
import me.wheelershigley.www.solace_fishing.api.statistics.UniformDistribution;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class ClimatePreference {
    public static final ClimatePreference DEFAULT_PREFERENCE = (new ClimatePreference.Builder()).build();

    private final Distribution
        temperaturePreference,
        humidityPreference,
        continentalnessPreference,
        erosionPreference,
        depthPreference,
        weirdnessPreference
    ;
    private final List< ResourceKey<Biome> > biomes;
    private final List< ResourceKey<Level> > dimensions;

    public ClimatePreference(
        Distribution temperaturePreference,
        Distribution humidityPreference,
        Distribution continentalnessPreference,
        Distribution erosionPreference,
        Distribution depthPreference,
        Distribution weirdnessPreference,
        List< ResourceKey<Biome> > biomesPreference,
        List< ResourceKey<Level> > dimensionsPreference
    ) {
        this.temperaturePreference = temperaturePreference;
        this.humidityPreference = humidityPreference;
        this.continentalnessPreference = continentalnessPreference;
        this.erosionPreference = erosionPreference;
        this.depthPreference = depthPreference;
        this.weirdnessPreference = weirdnessPreference;
        this.biomes = biomesPreference;
        this.dimensions = dimensionsPreference;
    }

    public boolean isInBounds(ClimateData locationData) {
        return temperaturePreference.isInBounds(        locationData.getTemperature()       )
            && humidityPreference.isInBounds(           locationData.getHumidity()          )
            && continentalnessPreference.isInBounds(    locationData.getContinentalness()   )
            && erosionPreference.isInBounds(            locationData.getErosion()           )
            && depthPreference.isInBounds(              locationData.getDepth()             )
            && weirdnessPreference.isInBounds(          locationData.getWeirdness()         )
            && (
                biomes.isEmpty()     || biomes.contains( locationData.getBiome() )
            ) && (
                dimensions.isEmpty() || dimensions.contains( locationData.getDimension() )
            )
        ;
    }

    public double getAverageWeightAt(ClimateData locationData) {
        double accumulator = 0.0;

        accumulator +=     temperaturePreference.get( locationData.getTemperature()     );
        accumulator +=        humidityPreference.get( locationData.getHumidity()        );
        accumulator += continentalnessPreference.get( locationData.getContinentalness() );
        accumulator +=         erosionPreference.get( locationData.getErosion()         );
        accumulator +=           depthPreference.get( locationData.getDepth()           );
        accumulator +=       weirdnessPreference.get( locationData.getWeirdness()       );

        return accumulator/6.0;
    }

    public double getAveragePickiness() {
        double accumulator = 0.0;

        accumulator += getPickiness(temperaturePreference       );
        accumulator += getPickiness(humidityPreference          );
        accumulator += getPickiness(continentalnessPreference   );
        accumulator += getPickiness(erosionPreference           );
        accumulator += getPickiness(depthPreference             );
        accumulator += getPickiness(weirdnessPreference         );

        return accumulator/6.0;
    }
    private double getPickiness(Distribution distribution) {
        if(distribution instanceof NormalDistribution) {
            return 1.0/( (NormalDistribution)distribution ).getStandardDeviation();
        }
        return 0.0;
    }

    public static class Builder {
        //Since the range is -1 to 1 (two), 0.5 preserves an area of 100%
        private static final Distribution NEUTRAL_DISTRIBUTION = new UniformDistribution(0.5);

        private Distribution
            temperaturePreference       = NEUTRAL_DISTRIBUTION,
            humidityPreference          = NEUTRAL_DISTRIBUTION,
            continentalnessPreference   = NEUTRAL_DISTRIBUTION,
            erosionPreference           = NEUTRAL_DISTRIBUTION,
            depthPreference             = NEUTRAL_DISTRIBUTION,
            weirdnessPreference         = NEUTRAL_DISTRIBUTION
        ;
        private final List< ResourceKey<Biome> > biomesPreference     = new ArrayList<>();
        private final List< ResourceKey<Level> > dimensionsPreference = new ArrayList<>();

        public ClimatePreference.Builder withTemperaturePreference(final Distribution temperaturePreference) {
            this.temperaturePreference = temperaturePreference;
            return this;
        }
        public ClimatePreference.Builder withHumidityPreference(final Distribution humidityPreference) {
            this.humidityPreference = humidityPreference;
            return this;
        }
        public ClimatePreference.Builder withContinentalnessPreference(final Distribution continentalnessPreference) {
            this.continentalnessPreference = continentalnessPreference;
            return this;
        }
        public ClimatePreference.Builder withErosionPreference(final Distribution erosionPreference) {
            this.erosionPreference = erosionPreference;
            return this;
        }
        public ClimatePreference.Builder withDepthPreference(final Distribution depthPreference) {
            this.depthPreference = depthPreference;
            return this;
        }
        public ClimatePreference.Builder withWeirdnessPreference(final Distribution weirdnessPreference) {
            this.weirdnessPreference = weirdnessPreference;
            return this;
        }

        public ClimatePreference.Builder withBiome(final ResourceKey<Biome> biomeKey) {
            biomesPreference.add(biomeKey);
            return this;
        }
        public ClimatePreference.Builder withBiomes(final List< ResourceKey<Biome> > biomeKeys) {
            biomesPreference.addAll(biomeKeys);
            return this;
        }
        public ClimatePreference.Builder withDimension(final ResourceKey<Level> dimensionKey) {
            dimensionsPreference.add(dimensionKey);
            return this;
        }
        public ClimatePreference.Builder withDimensions(final List< ResourceKey<Level> > dimensionKeys) {
            dimensionsPreference.addAll(dimensionKeys);
            return this;
        }

        public ClimatePreference build() {
            return new ClimatePreference(
                this.temperaturePreference,
                this.humidityPreference,
                this.continentalnessPreference,
                this.erosionPreference,
                this.depthPreference,
                this.weirdnessPreference,
                this.biomesPreference,
                this.dimensionsPreference
            );
        }
    }
}
