package me.wheelershigley.www.solace_jobs.mixins;

import me.wheelershigley.www.solace_jobs.data.ActionReward;
import me.wheelershigley.www.solace_jobs.data.ActionType;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(Animal.class)
public class BreedMixin {
    @Shadow
    public @Nullable ServerPlayer getLoveCause() {
        return null;
    }

    @Inject(
        method = "spawnChildFromBreeding",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Animal;finalizeSpawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;Lnet/minecraft/world/entity/AgeableMob;)V",
            shift = At.Shift.AFTER
        )
    )
    public void spawnChildFromBreeding(
        ServerLevel level, Animal partner,
        CallbackInfo ci
    ) {
        AtomicReference<ServerPlayer> cause = new AtomicReference<>();
        Optional.ofNullable(
            this.getLoveCause()
        ).or(
            () -> Optional.ofNullable( partner.getLoveCause() )
        ).ifPresent(cause::set);
        if(cause.get() == null) {
            return;
        }

        Animal animal = (Animal)(Object)this;

        Identifier identifier = BuiltInRegistries.ENTITY_TYPE.getKey( animal.getType() );
        Set<JobDefinition> jobs = JobsCodec.DATA.getJobs(identifier);
        for(JobDefinition job : jobs) {
            ActionReward reward = job.getFirstReward(identifier, ActionType.BREED);
            if(reward == null) {
                continue;
            }
            reward.give( cause.get(), job, animal.level().getRandom() );
        }
    }
}
