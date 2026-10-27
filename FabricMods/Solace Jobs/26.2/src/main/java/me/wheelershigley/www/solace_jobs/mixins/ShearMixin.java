package me.wheelershigley.www.solace_jobs.mixins;

import me.wheelershigley.www.solace_jobs.data.ActionReward;
import me.wheelershigley.www.solace_jobs.data.ActionType;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Entity.class)
public class ShearMixin {
    @Inject(
        method = "interact",
        at = @At("HEAD")
    )
    protected void mobInteract(
        Player player,
        InteractionHand hand,
        Vec3 location,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        if( !(player instanceof ServerPlayer serverPlayer) ) {
            return;
        }
        if( !(this instanceof Shearable shearable) ) {
            return;
        }

        if( !isShearing(shearable, serverPlayer, hand) ) {
            return;
        }

        LivingEntity livingEntity = ( (Mob)(Object)this ).asLivingEntity();
        if(livingEntity == null) {
            return;
        }

        Identifier identifier = BuiltInRegistries.ENTITY_TYPE.getKey( livingEntity.getType() );
        Set<JobDefinition> jobs = JobsCodec.DATA.getJobs(identifier);
        for(JobDefinition job : jobs) {
            ActionReward reward = job.getFirstReward(identifier, ActionType.SHEAR);
            if(reward == null) {
                continue;
            }
            reward.give( serverPlayer, job, serverPlayer.level().getRandom() );
        }
    }

    private boolean isShearing(
        Shearable shearable,
        ServerPlayer player, InteractionHand hand
    ) {
        ItemStack itemStack = player.getItemInHand(hand);
        return (
            itemStack.is(Items.SHEARS) && shearable.readyForShearing()
        );
    }
}
