package me.wheelershigley.www.diegetic.helper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class PlayerHelper {
    public static Vec3d getFootPosition(PlayerEntity player) {
        return player.getPos();
    }
}
