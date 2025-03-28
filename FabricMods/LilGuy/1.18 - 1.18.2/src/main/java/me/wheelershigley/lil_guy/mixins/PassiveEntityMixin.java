package me.wheelershigley.lil_guy.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PassiveEntity.class)
public class PassiveEntityMixin extends PathAwareEntity {
    protected PassiveEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow protected int happyTicksRemaining;
    @Shadow public int getBreedingAge() { return 0; }
    @Shadow public void setBreedingAge(int age) {}

    /**
     * @author Wheeler-Shigley
     * @reason Named baby animals will not have their age changed.
     */
    @Overwrite
    public void tickMovement() {
        super.tickMovement();

        if(this.getWorld().isClient) {
            if(0 < this.happyTicksRemaining) {
                if(this.happyTicksRemaining % 4 == 0) {
                    this.getWorld().addParticle(
                        ParticleTypes.HAPPY_VILLAGER,
                        this.getParticleX(1.0),
                        this.getRandomBodyY() + 0.5,
                        this.getParticleZ(1.0),
                        0.0,
                        0.0,
                        0.0
                    );
                }
                --this.happyTicksRemaining;
            }
        } else {
            if( this.isAlive() ) {
                int breeding_age = this.getBreedingAge();
                //Babies
                if(breeding_age < 0 && !this.hasCustomName() ) {
                    ++breeding_age;
                    this.setBreedingAge(breeding_age);
                }
                //Adults
                if(0 < breeding_age) {
                    --breeding_age;
                    this.setBreedingAge(breeding_age);
                }
            }
        }
    }
}
