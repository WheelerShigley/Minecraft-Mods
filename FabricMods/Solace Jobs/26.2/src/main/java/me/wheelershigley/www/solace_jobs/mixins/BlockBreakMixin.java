package me.wheelershigley.www.solace_jobs.mixins;

import me.wheelershigley.www.solace_jobs.data.ActionReward;
import me.wheelershigley.www.solace_jobs.data.ActionType;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(Block.class)
public class BlockBreakMixin {
    @Inject(
        method = "playerDestroy",
        at = @At("TAIL")
    )
    public void playerDestroy(
        Level level,
        Player player,
        BlockPos pos, BlockState state, BlockEntity blockEntity,
        ItemStack destroyedWith,
        CallbackInfo ci
    ) {
        if( !(player instanceof ServerPlayer serverPlayer) ) {
            return;
        }
        Identifier identifier = BuiltInRegistries.BLOCK.getKey( (Block)(Object)this );

        Set<JobDefinition> jobs = JobsCodec.DATA.getJobs(ActionType.BREAK);
        for(JobDefinition job : jobs) {
            ActionReward reward = job.getFirstReward(identifier);
            if(reward == null) {
                continue;
            }
            reward.give( serverPlayer, job, level.getRandom() );
        }
    }
}
