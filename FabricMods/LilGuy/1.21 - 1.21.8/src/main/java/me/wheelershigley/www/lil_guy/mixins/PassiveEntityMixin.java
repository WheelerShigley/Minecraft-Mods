package me.wheelershigley.www.lil_guy.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PassiveEntity.class)
public class PassiveEntityMixin extends PathAwareEntity {
    protected PassiveEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public int getBreedingAge() { return 0; }
    @Shadow public void setBreedingAge(int age) {}
    @Shadow protected int happyTicksRemaining;

    @Shadow @Final private static TrackedData<Boolean> CHILD;

    /**
     * @author Wheeler-Shigley
     * @reason Named baby animals will not have their age changed.
     */
    @Inject(
        method = "tickMovement",
        at = @At("HEAD"),
        cancellable = true
    )
    public void tickMovement(CallbackInfo ci) {
        super.tickMovement();
        if(this.getWorld().isClient) {
            if(this.happyTicksRemaining > 0) {
                if(this.happyTicksRemaining%4 == 0) {
                    this.getWorld().addParticleClient(
                        ParticleTypes.HAPPY_VILLAGER,
                        this.getParticleX( (double)1.0F ),
                        this.getRandomBodyY() + (double)0.5F,
                        this.getParticleZ( (double)1.0F ),
                        (double)0.0F,
                        (double)0.0F,
                        (double)0.0F
                    );
                }

                --this.happyTicksRemaining;
            }
        } else if( this.isAlive() ) {
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
        ci.cancel();
    }
}
