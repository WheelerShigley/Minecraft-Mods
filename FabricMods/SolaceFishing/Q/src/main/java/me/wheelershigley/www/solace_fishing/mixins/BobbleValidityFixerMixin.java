package me.wheelershigley.www.solace_fishing.mixins;

import me.wheelershigley.www.solace_fishing.implementations.CustomFishingRod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHook.class)
public abstract class BobbleValidityFixerMixin extends Projectile {
    public BobbleValidityFixerMixin(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @Inject(
        method = "shouldStopFishing",
        at = @At("HEAD"),
        cancellable = true
    )
    private void shouldStopFishing(Player owner, CallbackInfoReturnable<Boolean> cir) {
        if( !owner.canInteractWithLevel() ) {
            return;
        }

        ItemStack selectedItem        = owner.getMainHandItem();
        ItemStack selectedItemOffHand = owner.getOffhandItem();
        boolean mainHandIsFishing = selectedItem.getItem()        instanceof CustomFishingRod;
        boolean offHandIsFishing  = selectedItemOffHand.getItem() instanceof CustomFishingRod;

        if( (mainHandIsFishing || offHandIsFishing)
            && this.distanceToSqr(owner) <= (double)1024.0F
        ) {
            cir.setReturnValue(false);
        }
    }
}
