package me.wheelershigley.www.solace_fishing.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jspecify.annotations.NonNull;

public record ClimateData(
    double temperature,
    double humidity,
    double continentalness,
    double erosion,
    double depth,
    double weirdness
) {
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
        double mean = means.temperature();
        double deviation = 2.0*standard_deviations.temperature();
        if(this.temperature < mean-deviation || mean+deviation < this.temperature) {
            return false;
        }

        mean = means.humidity();
        deviation = standard_deviations.humidity();
        if(this.humidity < mean-deviation || mean+deviation < this.humidity) {
            return false;
        }

        mean = means.continentalness();
        deviation = standard_deviations.continentalness();
        if(this.continentalness < mean-deviation || mean+deviation < this.continentalness) {
            return false;
        }

        mean = means.erosion();
        deviation = standard_deviations.continentalness;
        if(this.continentalness < mean-deviation || mean+deviation < this.continentalness) {
            return false;
        }

        mean = means.depth();
        deviation = standard_deviations.depth();
        if(this.depth < mean-deviation || mean+deviation < this.depth) {
            return false;
        }

        mean = means.weirdness();
        deviation = standard_deviations.weirdness();
        if(this.weirdness < mean-deviation || mean+deviation < this.weirdness) {
            return false;
        }

        return true;
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

    private double percentize(double value) {
        return Math.round(10000.0*value)/100.0;
    }
}
