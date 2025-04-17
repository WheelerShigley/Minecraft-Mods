package me.wheelershigley.portal_network.mixins;

import me.wheelershigley.portal_network.CustomPortal;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Debug(export = true)
@Mixin(AbstractFireBlock.class)
public class IgniteReinforcedDeepslateMixin {

    @Inject(
        method = "onBlockAdded",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        if(  !oldState.isOf( state.getBlock() )  ) {
            Optional<CustomPortal> optional = CustomPortal.getNewPortal(world, pos, Direction.Axis.X);
            if( optional.isPresent() ) {
                ( (CustomPortal)optional.get() ).createPortal(world);
                ci.cancel(); //return;
            }
        }
    }

//    @Inject(
//        method = "canPlaceAt",
//        at = @At("HEAD")
//    )
//    private static void canPlaceAt(World world, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
//        PortalNetwork.LOGGER.info("a");
//        if( shouldLightCustomPortalAt(world, pos, direction) ) {
//            PortalNetwork.LOGGER.info("b");
//        }
//    }

    @Unique
    private static boolean shouldLightCustomPortalAt(World world, BlockPos pos, Direction direction) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        boolean bl = false;

        for (Direction direction2 : Direction.values()) {
            if(
                world.getBlockState( mutable.set(pos).move(direction2) ).isOf(Blocks.REINFORCED_DEEPSLATE)
            ) {
                bl = true;
                break;
            }
        }

        if(bl) {
            Direction.Axis axis;
            if( direction.getAxis().isHorizontal() ) {
                axis = direction.rotateYCounterclockwise().getAxis();
            } else {
                axis = Direction.Type.HORIZONTAL.randomAxis(world.random);
            }
            return CustomPortal.getNewPortal(world, pos, axis).isPresent();
        }
        return false;
    }
}
