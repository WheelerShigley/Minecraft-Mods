package me.wheelershigley.www.solace_jobs.mixins;

//import com.daqem.grieflogger.block.BlockHandler;
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
import net.minecraft.world.level.block.CropBlock;
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
        at = @At("HEAD")
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

        Block block = (Block)(Object)this;
        //If it's a plant, it must be fully-grown to be counted
        if(
            block instanceof CropBlock cropBlock
            && !cropBlock.isMaxAge(state)
        ) {
            return;
        }

        Identifier identifier = BuiltInRegistries.BLOCK.getKey(block);
        Set<JobDefinition> jobs = JobsCodec.DATA.getJobs(identifier);
        for(JobDefinition job : jobs) {
            ActionReward reward = job.getFirstReward(identifier, ActionType.BREAK);
            if(reward == null) {
                continue;
            }
            reward.give( serverPlayer, job, level.getRandom() );
        }
    }
}
