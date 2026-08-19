package me.wheelershigley.www.solace_fishing.mixins;

import me.wheelershigley.www.solace_fishing.data.AccessorizedFishingHook;
import me.wheelershigley.www.solace_fishing.data.RodAccessories;
import me.wheelershigley.www.solace_fishing.registrations.FishingItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHook.class)
public abstract class BellBobberRingMixin extends Projectile {
    public BellBobberRingMixin(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @Unique
    private int bite_time;
    @Shadow
    private boolean biting;

    @Inject(
        method = "tick",
        at = @At("TAIL")
    )
    public void tick(CallbackInfo ci) {
        if( this.level().isClientSide() ) {
            return;
        }

        if(this.biting) {
            bite_time++;

            if(1 < bite_time) {
                return;
            }

            RodAccessories accessories = ( (AccessorizedFishingHook)this ).solace_fishing$getAccessories();
            if(
                accessories == null
                || accessories.getBobber().isEmpty()
                || accessories.getBobber().getItem() != FishingItems.BELL_BOBBER
            ) {
                return;
            }

            this.level().playSound(
                this,
                this.blockPosition(),
                SoundEvents.NOTE_BLOCK_BELL.value(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            );
        } else {
            bite_time = 0;
        }
    }
}
