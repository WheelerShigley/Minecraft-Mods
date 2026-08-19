package me.wheelershigley.www.solace_fishing.api.fishing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ClimateData {
    private double
        temperature,
        humidity,
        continentalness,
        erosion,
        depth,
        weirdness
    ;
    private Holder<Biome> biome = null;
    private ResourceKey<Level> dimension = null;

    public ClimateData(
        double temperature, double humidity,
        double continentalness, double erosion,
        double depth, double weirdness,
        @Nullable Holder<Biome> biome,
        @Nullable ResourceKey<Level> dimension
    ) {
        setTemperature(temperature);
        setHumidity(humidity);
        setContinentalness(continentalness);
        setErosion(erosion);
        setDepth(depth);
        setWeirdness(weirdness);
        this.biome = biome;
        this.dimension = dimension;
    }

    public static ClimateData sample(ServerLevel level, BlockPos pos) {
        int blockX = QuartPos.toBlock( pos.getX() );
        int blockY = QuartPos.toBlock( pos.getY() );
        int blockZ = QuartPos.toBlock( pos.getZ() );
        DensityFunction.SinglePointContext context = new DensityFunction.SinglePointContext(
            blockX, blockY, blockZ
        );

        Climate.Sampler sampler = level.getChunkSource().getGeneratorState().randomState().sampler();
        return new ClimateData(
            sampler.temperature().compute(context),
            sampler.humidity().compute(context),
            sampler.continentalness().compute(context),
            sampler.erosion().compute(context),
            normalizeDepth( sampler.depth().compute(context) ),
            sampler.weirdness().compute(context),
            level.getBiome(pos),
            level.dimension()
        );
    }

    public boolean isInDoubleBounds(ClimateData means, ClimateData standard_deviations) {
        double mean = means.getTemperature();
        double deviation = 2.0*standard_deviations.getTemperature();
        if(mean+deviation < this.temperature || this.temperature < mean-deviation) {
            return false;
        }

        mean = means.getHumidity();
        deviation = 2.0*standard_deviations.getHumidity();
        if(mean+deviation < this.humidity || this.humidity < mean-deviation) {
            return false;
        }

        mean = means.getContinentalness();
        deviation = 2.0*standard_deviations.getContinentalness();
        if(mean+deviation < this.continentalness || this.continentalness < mean-deviation) {
            return false;
        }

        mean = means.getErosion();
        deviation = 2.0*standard_deviations.getErosion();
        if(mean+deviation < this.erosion || this.erosion < mean-deviation) {
            return false;
        }

        mean = means.getDepth();
        deviation = 2.0*standard_deviations.getDepth();
        if(mean+deviation < this.depth || this.depth < mean-deviation) {
            return false;
        }

        mean = means.getWeirdness();
        deviation = 2.0*standard_deviations.getWeirdness();
        if(mean+deviation < this.weirdness || this.weirdness < mean-deviation) {
            return false;
        }

        return true;
    }

    private static double normalizeDepth(double raw_depth) {
        /* The range of depth, normally is -2 to 1
           This can be transformed to match the other data-points by the form
           d_1 = (1/3) * (2*d_0+1)
         */
        return ( 2.0*raw_depth + 1.0 )  /  3.0;
    }

    public double getTemperature() {
        return temperature;
    }
    public double getHumidity() {
        return humidity;
    }
    public double getContinentalness() {
        return continentalness;
    }
    public double getErosion() {
        return erosion;
    }
    public double getDepth() {
        return depth;
    }
    public double getWeirdness() {
        return weirdness;
    }

    public Holder<Biome> getBiome() {
        return biome;
    }
    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public void setTemperature(double temperature) {
        this.temperature = Math.clamp(temperature, -1.0, 1.0);
    }
    public void setHumidity(double humidity) {
        this.humidity = Math.clamp(humidity, -1.0, 1.0);
    }
    public void setContinentalness(double continentalness) {
        this.continentalness = Math.clamp(continentalness, -1.0, 1.0);
    }
    public void setErosion(double erosion) {
        this.erosion = Math.clamp(erosion, -1.0, 1.0);
    }
    public void setDepth(double depth) {
        this.depth = Math.clamp(depth, -1.0, 1.0);
    }
    public void setWeirdness(double weirdness) {
        this.weirdness = Math.clamp(weirdness, -1.0, 1.0);
    }

    @Override
    public @NonNull String toString() {
        return
            "ClimateData = {temperature: " +
            percentize(temperature) +
            "%, humidity: " +
            percentize(humidity) +
            "%, continentalness: " +
            percentize(continentalness) +
            "%, erosion: " +
            percentize(erosion) +
            "%, depth: " +
            percentize(depth) +
            "%, weirdness: " +
            percentize(weirdness) +
            "%}"
        ;
    }

    public static double percentize(double value) {
        return Math.round(10000.0*value)/100.0;
    }

    @Override
    public ClimateData clone() {
        return new ClimateData(
            this.temperature,
            this.humidity,
            this.continentalness,
            this.erosion,
            this.depth,
            this.weirdness,
            this.biome,
            this.dimension
        );
    }

    public static class Builder {
        private ClimateData data = new ClimateData(
            0.0, 0.0, 0.0, 0.0, 62, 0.0,
            null, null
        );

        public Builder of(ClimateData data) {
            this.data = data.clone();
            return this;
        }

        public Builder withDepth(@NotNull Level level) {
            data.setDepth( level.getSeaLevel() );
            return this;
        }
        public Builder withDepth(double depth) {
            data.setDepth(depth);
            return this;
        }

        public Builder withTemperature(double temperature) {
            data.setTemperature(temperature);
            return this;
        }
        public Builder withHumidity(double humidity) {
            data.setHumidity(humidity);
            return this;
        }
        public Builder withContinentalness(double continentalness) {
            data.setContinentalness(continentalness);
            return this;
        }
        public Builder withErosion(double erosion) {
            data.setErosion(erosion);
            return this;
        }
        public Builder withWeirdness(double weirdness) {
            data.setWeirdness(weirdness);
            return this;
        }

        public Builder withBiome(@NotNull Holder<Biome> biome) {
            data.biome = biome;
            return this;
        }
        public Builder withDimension(@NotNull ResourceKey<Level> level) {
            data.dimension = level;
            return this;
        }

        public ClimateData build() {
            return data;
        }
    }
}
