package me.wheelershigley.www.solace_jobs.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import me.wheelershigley.www.solace_jobs.data.ActionReward;
import me.wheelershigley.www.solace_jobs.data.ActionType;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
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
import java.util.Set;

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
            Identifier identifier = BuiltInRegistries.ITEM.getKey( item.getItem() );
            Set<JobDefinition> jobs = JobsCodec.DATA.getJobs(identifier);
            for(JobDefinition job : jobs) {
                ActionReward reward = job.getFirstReward(identifier, ActionType.FISH);
                if(reward == null) {
                    continue;
                }
                reward.give( serverPlayer, job, serverPlayer.level().getRandom() );
            }
        }
    }
}