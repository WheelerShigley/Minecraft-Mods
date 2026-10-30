package me.wheelershigley.www.solace_jobs.mixins;

import com.daqem.grieflogger.database.service.Services;
import com.daqem.grieflogger.model.Operation;
import com.daqem.grieflogger.model.history.IHistory;
import me.wheelershigley.www.solace_jobs.rewards.ActionType;
import me.wheelershigley.www.solace_jobs.rewards.RewardsHelper;
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

import java.util.List;

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

        List<IHistory> history = Services.BLOCK.getBlockHistory(
            serverPlayer.level(),
            pos
        );
        if( wasPlacedByPlacer(history) ) {
            return;
        }

        Identifier targetIdentifier = BuiltInRegistries.BLOCK.getKey(block);
        RewardsHelper.attemptReward(
            serverPlayer,
            targetIdentifier,
            ActionType.BREAK,
            serverPlayer.level().getRandom()
        );
    }

    private static boolean wasPlacedByPlacer(List<IHistory> history) {
        // This is an imperfect check, please refer to
        // https://github.com/DAQEM/GriefLogger/issues/190
        // for details
        for(IHistory historyItem : history) {
            if( historyItem.getAction().getOperation().equals(Operation.ADD) ) {
                return true;
            }
        }
        return false;
    }
}
