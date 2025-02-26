package me.wheelershigley.live_catch;

import net.minecraft.util.math.Vec3d;

public class ProjectileMotionHelper {
    public static Vec3d calculateVelocity(Vec3d start, Vec3d end, Vec3d current) {
        double total_xz_distance = calculateXZDistance(start, end);
        double speed; {
            speed = calculateXZDistance(current, start);
            speed /= total_xz_distance;
            speed -= 1.0;
            speed *= -2.0*(end.y/total_xz_distance);
        }

        double x_direction = end.x - current.x;
        double z_direction = end.z - current.z;
        double xz_distance = Math.sqrt(x_direction*x_direction + z_direction*z_direction);
        x_direction /= xz_distance;
        z_direction /= xz_distance;

        LiveCatch.LOGGER.info("\nVelocity: ("+x_direction+", "+speed+", "+z_direction+")");

        return new Vec3d(x_direction, speed, z_direction);
    }

    private static Vec3d position(double percentage, Vec3d start, Vec3d end) {
        double total_xz_distance = calculateXZDistance(start, end);
        double height; {
            height = percentage-1.0;
            height *= total_xz_distance;
            height *= height;
            height *= -1.0*(end.y/total_xz_distance/total_xz_distance);
            height += end.y;
        }

        double x_position = percentage * end.x;
        x_position += (1.0-percentage) * start.x;

        double z_position = percentage * end.z;
        z_position += (1.0-percentage) * start.z;

        return new Vec3d(x_position, height, z_position);
    }

    public static double calculateXZDistance(Vec3d start, Vec3d end) {
        double dx = start.x - end.x;
        double dz = start.z - end.z;
        return Math.abs( Math.sqrt(dx*dx + dz*dz) );
    }

}
