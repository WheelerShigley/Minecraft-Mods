package me.wheelershigley.www.solace_jobs.mixins;

import me.wheelershigley.www.solace_jobs.rewards.ActionType;
import me.wheelershigley.www.solace_jobs.rewards.RewardsHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class KillMixin {
    @Inject(
        method = "awardKillScore",
        at = @At("TAIL")
    )
    public void awardKillScore(
        Entity victim,
        DamageSource killingBlow,
        CallbackInfo ci
    ) {
        if( !(killingBlow.getDirectEntity() instanceof ServerPlayer serverPlayer) ) {
            return;
        }

        Identifier targetIdentifier = BuiltInRegistries.ENTITY_TYPE.getKey( victim.getType() );
        RewardsHelper.attemptReward(
            serverPlayer,
            targetIdentifier,
            ActionType.KILL,
            serverPlayer.level().getRandom()
        );

    }
}
