package me.wheelershigley.www.window.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class LevelHelper {
    public static BlockPos getSafeRTPLocation(Level level) {
        //TODO
        return getRandomSurfacePosition(level);
    }
    private static BlockPos getRandomSurfacePosition(Level level) {
        double x_percentage = 2.0 * level.getRandom().nextDouble() - 1.0;
        double z_percentage = 2.0 * level.getRandom().nextDouble() - 1.0;

        int x_position = (int)( x_percentage * level.getWorldBorder().getMaxX() );
        int z_position = (int)( z_percentage * level.getWorldBorder().getMaxZ() );
        int y_position = level.getHeight(Heightmap.Types.WORLD_SURFACE, x_position, z_position);

        return new BlockPos(x_position, y_position, z_position);
    }
}
