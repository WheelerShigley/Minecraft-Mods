package me.wheelershigley.www.window.mixins;

import me.wheelershigley.www.window.WindowConfig;
import me.wheelershigley.www.window.api.PortalDefinition;
import me.wheelershigley.www.window.portal.CustomPortal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(Level.class)
public class IgnitionAttemptMixin {
    @Inject(
        method = "setBlockAndUpdate",
        at = @At("TAIL")
    )
    public void setBlockAndUpdate(
        BlockPos pos, BlockState blockState,
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
        /*PortalShape shape = null;
        for(Block frameMaterial : frameMaterials) {
            shape = getPotentialPortalShape( (Level)(Object)this, pos, Direction.NORTH, frameMaterial);
            if(shape != null) {
                break;
            }
        }
        if(shape == null) {
            return;
        }*/
        System.out.println("IgnitionAttemptMixin.setBlockAndUpdate");
        boolean worked = false;
        for(Block material : frameMaterials) {
             worked = CustomPortal.attemptPortal(
                (Level)(Object)this, pos,
                material, blockState.getBlock()
            );
             if(worked) {
                 return;
             }
        }
    }

    // Based on BaseFireBlock::isPortal
    private static @Nullable PortalShape getPotentialPortalShape(
        final Level level, final BlockPos pos,
        final Direction forwardDirection, Block frameMaterial
    ) {
        BlockPos.MutableBlockPos testPos = pos.mutable();
        boolean hasAdjacentFrame = false;
        Direction direction = null;

        for(Direction face : Direction.values() ) {
            if( level.getBlockState(
                    testPos.set(pos).move(face)
                ).is(frameMaterial)
            ) {
                hasAdjacentFrame = true;
                direction = face;
                break;
            }
        }

        if( !hasAdjacentFrame || direction == null ) {
            return null;
        } else {
            return PortalShape.findAnyShape(level, pos, direction.getAxis() );
        }
    }
}
