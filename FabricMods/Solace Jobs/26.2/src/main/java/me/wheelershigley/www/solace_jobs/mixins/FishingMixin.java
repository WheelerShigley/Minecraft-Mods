package me.wheelershigley.www.solace_jobs.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import me.wheelershigley.www.solace_jobs.rewards.ActionType;
import me.wheelershigley.www.solace_jobs.rewards.RewardsHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FishingHook.class)
public abstract class FishingMixin {
    @Inject(
        method = "retrieve",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancements/triggers/FishingRodHookedTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/projectile/FishingHook;Ljava/util/Collection;)V",
            shift = At.Shift.AFTER
        )
    )
    private void onFishCaught(
        ItemStack rod,
        CallbackInfoReturnable<Integer> cir,
        @Local List<ItemStack> items
    ) {
        FishingHook hook = (FishingHook)(Object)this;
        assert hook != null;
        if( !(hook.getPlayerOwner() instanceof ServerPlayer serverPlayer) ) {
            return;
        }

        for(ItemStack item : items) {
            Identifier targetIdentifier = BuiltInRegistries.ITEM.getKey( item.getItem() );
            RewardsHelper.attemptReward(
                serverPlayer,
                targetIdentifier,
                ActionType.FISH,
                serverPlayer.level().getRandom()
            );
        }

    }
}