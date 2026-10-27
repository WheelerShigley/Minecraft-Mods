package me.wheelershigley.www.solace_jobs.mixins;

import me.wheelershigley.www.solace_jobs.data.ActionReward;
import me.wheelershigley.www.solace_jobs.data.ActionType;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(BrushableBlockEntity.class)
public class BlockBrushMixin {

    @Inject(
        method = "brushingCompleted",
        at = @At("TAIL")
    )
    private void brushingCompleted(
        ServerLevel level,
        LivingEntity user,
        ItemStack brush,
        CallbackInfo ci
    ) {
        if( !(user instanceof ServerPlayer serverPlayer) ) {
            return;
        }

        Block block = ( (BlockEntity)(Object)this ).getBlockState().getBlock();

        Identifier identifier = BuiltInRegistries.BLOCK.getKey(block);
        Set<JobDefinition> jobs = JobsCodec.DATA.getJobs(identifier);
        for(JobDefinition job : jobs) {
            ActionReward reward = job.getFirstReward(identifier, ActionType.BRUSH);
            if(reward == null) {
                continue;
            }
            reward.give( serverPlayer, job, level.getRandom() );
        }
    }
}
