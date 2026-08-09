package me.wheelershigley.www.solace_fishing.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jspecify.annotations.NonNull;

public record ClimateData(
    float temperature,
    float humidity,
    float continentalness,
    float erosion,
    float depth,
    float weirdness
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
            (float) sampler.temperature().compute(context),
            (float)sampler.humidity().compute(context),
            (float)sampler.continentalness().compute(context),
            (float)sampler.erosion().compute(context),
            (float)sampler.depth().compute(context),
            (float)sampler.weirdness().compute(context)
        );
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

    private float percentize(float value) {
        return Math.round(10000.0f*value)/100.0f;
    }
}
