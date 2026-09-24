package me.wheelershigley.www.window.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.Set;

public class LevelHelper {
    public static final int MAXIMUM_ATTEMPT_COUNT = 1024;
    private static final Set<Block> DANGEROUS_BLOCKS = Set.of(
        Blocks.LAVA,
        Blocks.LAVA_CAULDRON,
        Blocks.FIRE,
        Blocks.SOUL_FIRE,
        Blocks.CACTUS,
        Blocks.SWEET_BERRY_BUSH,
        Blocks.MAGMA_BLOCK,
        Blocks.CAMPFIRE,
        Blocks.SOUL_CAMPFIRE,
        Blocks.POWDER_SNOW,
        Blocks.POWDER_SNOW_CAULDRON,
        Blocks.WITHER_ROSE
    );

    public static BlockPos getSafeRTPLocation(Level level) {
        BlockPos position = getRandomSurfacePosition(level);

        int attempt_count = 0;
        while(
            attempt_count < MAXIMUM_ATTEMPT_COUNT
            && isPotentiallyDangerous(position, level)
        ) {
            position = getRandomSurfacePosition(level);
            attempt_count++;
        }

        return position;
    }
    private static BlockPos getRandomSurfacePosition(Level level) {
        WorldBorder border = level.getWorldBorder();

        int x_position = (int)(
            border.getMinX() +
            level.getRandom().nextDouble() * (
                border.getMaxX() - border.getMinX()
            )
        );
        int z_position = (int)(
            border.getMinZ() +
            level.getRandom().nextDouble() * (
                border.getMaxZ() - border.getMinZ()
            )
        );
        int y_position = level.getHeight(Heightmap.Types.WORLD_SURFACE, x_position, z_position);

        return new BlockPos(x_position, y_position, z_position);
    }
    private static boolean isPotentiallyDangerous(BlockPos position, Level level) {
        AABB volume = new AABB(
            position.getX() - 1.5,
            position.getY() - 1.5,
            position.getZ() - 1.5,
            position.getX() + 1.5,
            position.getY() + 2.5,
            position.getZ() + 1.5
        );
        return level
            .getBlockStates(volume)
            .anyMatch(
                state -> DANGEROUS_BLOCKS.contains( state.getBlock() )
            )
        ;
    }
}
