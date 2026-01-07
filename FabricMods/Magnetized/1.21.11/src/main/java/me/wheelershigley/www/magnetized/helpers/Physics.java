package me.wheelershigley.www.magnetized.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Physics {

    private static final double WATER_DENSITY = 1000; // kg/m^3
    private static final double STONE_DENSITY = 2710; // kg/m^3
    /**
     * Finds the (approximate) density of an Entity
     *
     * @param entity entity with bounding box (volume, m^3)
     * @return mass, in kilograms (kg), of entity's bounding-box (with an assumed density)
     */
    public static double getMass(Entity entity) {
        // v = dx*dy*dz
        double Volume; /*m^3*/ {
            Box boundingBox = entity.getBoundingBox();
            Volume =
                (boundingBox.maxX - boundingBox.minX)
                * (boundingBox.maxY - boundingBox.minY)
                * (boundingBox.maxZ - boundingBox.minZ)
            ;
        }

        // v*p => volume * density <=> { m^3 * kg/m^3 = kg }
        return Volume * (
            (entity instanceof LivingEntity ? WATER_DENSITY : STONE_DENSITY)
        );
    }

    private static final double GRAVITATIONAL_CONSTANT = 0.000_000_000_667_430; //(+/- 1.5e-7) m^3 / (kg * s^2)
    /**
     * @param massOne mass of first object
     * @param massTwo mass of second object
     * @param distance distance between both objects
     * @return gravitational attraction, in Newtons, between both objects
     */
    public static double getAttraction(double massOne, double massTwo, double distance) {
        /* Gravity [Force] Formula:
            F = G * m_1 * m_2 / d^2, where
            G is the Gravitation Constant,
            m_1 and m_2 are the two masses,
            and d is the distance between the two objects
         */
        return
            GRAVITATIONAL_CONSTANT
            * (massOne * massTwo)
            / (distance * distance)
        ;
    }

    public static double getAcceleration(double force, double mass) {
        /* Acceleration [m/(s^2)] Formula:
            Force = mass * acceleration => acceleration = Force / mass
         */
        return force/mass;
    }
    public static double getSpeed(double acceleration, double time_change) {
        /* Speed [m/s] Formula:
            da/dt = ({m/s}_final - {m/s}_initial)*(s_final - s_initial) <=> { m/(s^2) * s = m/s }
         */
        return acceleration * time_change;
    }
    public static Vec3d getVelocity(double speed, Vec3d direction) {
        double distance = Math.abs( direction.distanceTo(Vec3d.ZERO) );
        Vec3d normalized_direction = new Vec3d(
            direction.x/distance,
            direction.y/distance,
            direction.z/distance
        );

        return new Vec3d(
            speed * normalized_direction.x,
            speed * normalized_direction.y,
            speed * normalized_direction.z
        );
    }

    /**
     *
     * @param distance distance
     * @return this will multiple speed by zero the closer the distance is to zero (non-linearly)
     */
    private static double fantasySlowDown(double distance) {
        distance = Math.abs(distance);
        if(distance <= 1) {
            return 0;
        }
        return 1 - 1.0/(distance*distance);
    }

    private static final double SPEED_MULTIPLIER_PER_LEVEL = 16_666_666; //50_000_000/MAX_LEVEL (50M/3)
    private static int particle_frequency_counter = 0;
    public static void attractOneEntityToAnother(Entity datum, Entity dynamic, int level) {
        double speed; {
            double datumMass   = getMass(datum);
            double dynamicMass = getMass(dynamic);

            double gravitational_force; {
                double distance = dynamic.getEntityPos().distanceTo( datum.getEntityPos() );
                gravitational_force = getAttraction(datumMass, dynamicMass, distance );
            }

            double acceleration = getAcceleration(gravitational_force, dynamicMass);
            double seconds_per_tick = 1.0/datum.getEntityWorld().getTickManager().getTickRate();
            speed = getSpeed(acceleration, seconds_per_tick);
        }

        Vec3d direction; {
            Vec3d datumPosition = datum.getEntityPos();
            Vec3d dynamicPosition = dynamic.getEntityPos();
            direction = new Vec3d(
                datumPosition.x - dynamicPosition.x,
                datumPosition.y - dynamicPosition.y,
                datumPosition.z - dynamicPosition.z
            );
        }

        /*fantasy*/ {
            //SPEED_MULTIPLIER is a fantasy addition to let the speeds be realistically observe-able, in-game
            speed *= SPEED_MULTIPLIER_PER_LEVEL * level;
            speed *= fantasySlowDown(  datum.getEntityPos().distanceTo( dynamic.getEntityPos() )  );

            //spawn particles
            World world = dynamic.getEntityWorld();
            particle_frequency_counter++;
            if(
                world instanceof ServerWorld
                && particle_frequency_counter % 4 == 0 //25% as often as always
            ) {
                ( (ServerWorld)world ).spawnParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    false, true,
                    dynamic.getX(), dynamic.getBodyY(1.0), dynamic.getZ(),
                    1,
                    0.05*direction.x * world.random.nextGaussian(),
                    0.05*direction.y * world.random.nextGaussian(),
                    0.05*direction.z * world.random.nextGaussian(),
                    0.02 * world.random.nextGaussian()
                );
                particle_frequency_counter = 1;
            }

            //play sound
            world.playSound(
                dynamic,
                dynamic.getX(), dynamic.getY(), dynamic.getZ(),
                SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                SoundCategory.PLAYERS,
                0.25f, 1.5f,
                world.getRandom().nextLong()
            );
        }

        dynamic.velocityDirty = true;
        dynamic.setVelocity( getVelocity(speed, direction) );
    }
}
