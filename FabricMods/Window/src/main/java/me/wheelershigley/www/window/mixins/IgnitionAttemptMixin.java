package me.wheelershigley.www.window.mixins;

import me.wheelershigley.www.window.WindowConfig;
import me.wheelershigley.www.window.api.PortalDefinition;
import me.wheelershigley.www.window.portal.Portal;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(Level.class)
public abstract class IgnitionAttemptMixin {
    @Shadow
    @Final
    private ResourceKey<Level> dimension;

    @Inject(
        method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
        at = @At("TAIL")
    )
    public void setBlock(
        BlockPos pos, BlockState blockState, int updateFlags,
        CallbackInfoReturnable<Boolean> cir
    ) {
        Set<PortalDefinition> validDefinitions = new HashSet<>();
        for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
            if(
                definition.ignitionMaterial().equals( blockState.getBlock() )
                && (
                    this.dimension == definition.fromDimension()
                    || this.dimension == definition.toDimension()
                )
            ) {
                validDefinitions.add(definition);
            }
        }

        // Attempt Ignition
        for(PortalDefinition definition : validDefinitions) {
            boolean worked = Portal.attemptPortal(
                (Level)(Object)this, pos,
                definition.frameMaterial(), definition.ignitionMaterial(),
                definition.color()
            );
            if(worked) {
                return;
            }
        }
    }
}
