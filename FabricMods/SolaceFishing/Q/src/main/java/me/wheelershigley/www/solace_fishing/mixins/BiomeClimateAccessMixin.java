package me.wheelershigley.www.solace_fishing.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Biome.class)
public interface BiomeClimateAccessMixin {
    @Invoker("getHeightAdjustedTemperature")
    float invokeGetHeightAdjustedTemperature(BlockPos pos, int seaLevel);
}
