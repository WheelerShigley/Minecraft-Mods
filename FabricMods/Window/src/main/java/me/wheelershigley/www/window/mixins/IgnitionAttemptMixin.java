package me.wheelershigley.www.window.mixins;

import me.wheelershigley.www.window.WindowConfig;
import me.wheelershigley.www.window.api.PortalDefinition;
import me.wheelershigley.www.window.portal.Portal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(Level.class)
public class IgnitionAttemptMixin {
    @Inject(
        method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
        at = @At("TAIL")
    )
    public void setBlock(
        BlockPos pos, BlockState blockState, int updateFlags,
        CallbackInfoReturnable<Boolean> cir
    ) {
        Set<Block> frameMaterials = new HashSet<>();

        boolean ignition = false;
        for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
            if(  definition.ignitionMaterial().equals( blockState.getBlock() )  ) {
                ignition = true;
                frameMaterials.add( definition.frameMaterial() );
            }
        }
        if(!ignition) {
            return;
        }

        // Attempt Ignition
        boolean worked = false;
        for(Block material : frameMaterials) {
             worked = Portal.attemptPortal(
                (Level)(Object)this, pos,
                material, blockState.getBlock()
            );
             if(worked) {
                 return;
             }
        }
    }
}
