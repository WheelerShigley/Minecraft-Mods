package me.wheelershigley.www.lil_guy.mixins;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Tadpole.class)
public abstract class TadpoleMixin extends AbstractFish {
    public TadpoleMixin(EntityType<? extends AbstractFish> entityType, Level world) {
        super(entityType, world);
    }

    /**
     * @author Wheeler-Shigley
     * @reason Tadpoles are not Baby Frogs, but they should behave like them: not ageing when named.
     */
    @Inject(
        method = "aiStep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/frog/Tadpole;setAge(I)V"
        ),
        cancellable = true
    )
    public void aiStep(CallbackInfo ci) {
        if( this.hasCustomName() ) {
            ci.cancel();
        }
    }
}
