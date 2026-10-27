package me.wheelershigley.www.solace_jobs.mixins;

import me.wheelershigley.www.solace_jobs.data.ActionReward;
import me.wheelershigley.www.solace_jobs.data.ActionType;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

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

        Entity entity = (Entity)(Object)this;

        Identifier identifier = BuiltInRegistries.ENTITY_TYPE.getKey( entity.getType() );
        Set<JobDefinition> jobs = JobsCodec.DATA.getJobs(identifier);
        for(JobDefinition job : jobs) {
            ActionReward reward = job.getFirstReward(identifier, ActionType.KILL);
            if(reward == null) {
                continue;
            }
            reward.give( serverPlayer, job, victim.level().getRandom() );
        }

    }
}
