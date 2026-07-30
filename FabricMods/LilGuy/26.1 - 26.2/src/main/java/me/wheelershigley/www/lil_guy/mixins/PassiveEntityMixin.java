package me.wheelershigley.www.lil_guy.mixins;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AgeableMob.class)
public class PassiveEntityMixin extends PathfinderMob {
    protected PassiveEntityMixin(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    /**
     * @author Wheeler-Shigley
     * @reason Named, baby animals will not have their age changed.
     */
    @Inject(
        method = "canAgeUp",
        at = @At("RETURN"),
        cancellable = true
    )
    public void canAgeUp(CallbackInfoReturnable<Boolean> cir) {
        if( this.hasCustomName() ) {
            cir.setReturnValue(false);
        }
    }
}
