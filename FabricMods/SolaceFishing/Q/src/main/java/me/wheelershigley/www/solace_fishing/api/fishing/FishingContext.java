package me.wheelershigley.www.solace_fishing.api.fishing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

public record FishingContext(
    Block medium,
    int medium_depth,
    Item rod,
    float luck,
    RodAccessories accessories,
    ClimateData environment,
    boolean isOpenWater
) {
    public static int discoverDepthOfMedium(Level level, BlockPos position) {
        int accumulator = 0;

        final Block MEDIUM = level.getBlockState(position).getBlock();
        while(
            level.getBlockState(position).getBlock().equals(MEDIUM)
            && level.getMinY() < position.getY() && position.getY() < level.getMaxY()
        ) {
            position = position.below();
            accumulator++;
        }

        return accumulator;
    }

    @Override
    public @NonNull String toString() {
        return
            "FishingContext [" +
            "medium: " + medium +
            ", rod: " + rod +
            ", luck: " + luck +
            ", accessories: " + accessories +
            ", environment: " + environment +
            ", open-water: " + isOpenWater +
            "]"
        ;
    }
}
