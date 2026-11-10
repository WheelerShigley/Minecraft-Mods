package me.wheelershigley.www.solace_jobs.mixins;

import me.wheelershigley.www.solace_jobs.rewards.ActionType;
import me.wheelershigley.www.solace_jobs.rewards.RewardsHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "noobanidus.mods.lootr.common.block.entity.LootrBrushableBlockEntity")
public class LootrBrushMixin {
    @Inject(
        method = "brushingCompleted",
        at = @At("TAIL")
    )
    public void brushingCompleted(
        Player player,
        CallbackInfo ci
    ) {
        if( !(player instanceof ServerPlayer serverPlayer) ) {
            return;
        }

        Block block = ( (BlockEntity)(Object)this ).getBlockState().getBlock();

        Identifier targetIdentifier = BuiltInRegistries.BLOCK.getKey(block);
        RewardsHelper.attemptReward(
            serverPlayer,
            targetIdentifier,
            ActionType.BRUSH
        );
    }
}
