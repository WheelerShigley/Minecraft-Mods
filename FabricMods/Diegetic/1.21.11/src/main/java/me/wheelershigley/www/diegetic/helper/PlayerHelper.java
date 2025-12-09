package me.wheelershigley.www.diegetic.helper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class PlayerHelper {
    public static Vec3d getFootPosition(PlayerEntity player) {
        Vec3d position = player.getEyePos();
        position.add(0.0, -1.0, 0.0);
        return position;
    }
}
