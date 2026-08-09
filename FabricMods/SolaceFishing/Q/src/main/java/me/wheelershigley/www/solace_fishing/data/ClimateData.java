package me.wheelershigley.www.solace_fishing.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class ClimateData {
    private double
        temperature,
        humidity,
        continentalness,
        erosion,
        depth,
        weirdness
    ;

    public static final ClimateData DEFAULT_MEANS      = new ClimateData(0.0, 0.0, 0.0, 0.0, 62.0,  0.0);
    public static final ClimateData DEFAULT_DEVIATIONS = new ClimateData(0.7, 0.7, 0.7, 0.7, 100.0, 0.7);

    public ClimateData(
        double temperature, double humidity,
        double continentalness, double erosion,
        double depth, double weirdness
    ) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.continentalness = continentalness;
        this.erosion = erosion;
        this.depth = depth;
        this.weirdness = weirdness;
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
            sampler.depth().compute(context),
            sampler.weirdness().compute(context)
        );
    }

    public boolean isInDoubleBounds(ClimateData means, ClimateData standard_deviations) {
        double mean = means.getTemperature();
        double deviation = 2.0*standard_deviations.getTemperature();
        if(this.temperature < mean-deviation || mean+deviation < this.temperature) {
            return false;
        }

        mean = means.getHumidity();
        deviation = standard_deviations.getHumidity();
        if(this.humidity < mean-deviation || mean+deviation < this.humidity) {
            return false;
        }

        mean = means.getContinentalness();
        deviation = standard_deviations.getContinentalness();
        if(this.continentalness < mean-deviation || mean+deviation < this.continentalness) {
            return false;
        }

        mean = means.getErosion();
        deviation = standard_deviations.getContinentalness();
        if(this.continentalness < mean-deviation || mean+deviation < this.continentalness) {
            return false;
        }

        mean = means.getDepth();
        deviation = standard_deviations.getDepth();
        if(this.depth < mean-deviation || mean+deviation < this.depth) {
            return false;
        }

        mean = means.getWeirdness();
        deviation = standard_deviations.getWeirdness();
        if(this.weirdness < mean-deviation || mean+deviation < this.weirdness) {
            return false;
        }

        return true;
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

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }
    public void setContinentalness(double continentalness) {
        this.continentalness = continentalness;
    }
    public void setErosion(double erosion) {
        this.erosion = erosion;
    }
    public void setDepth(double depth) {
        this.depth = depth;
    }
    public void setWeirdness(double weirdness) {
        this.weirdness = weirdness;
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
            ", weirdness: " +
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
            this.weirdness
        );
    }

    public static class Builder {
        private ClimateData data = new ClimateData(0.0, 0.0, 0.0, 0.0, 62, 0.0);

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

        public ClimateData build() {
            return data;
        }
    }
}
