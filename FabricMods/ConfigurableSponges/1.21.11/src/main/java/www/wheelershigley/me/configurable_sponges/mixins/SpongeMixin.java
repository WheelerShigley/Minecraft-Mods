package www.wheelershigley.me.configurable_sponges.mixins;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.rule.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraft.block.Block.dropStacks;
import static www.wheelershigley.me.configurable_sponges.utils.MathFunctions.CenteredOctahedralNumber;
import static www.wheelershigley.me.configurable_sponges.gamerules.GameRuleRegistrator.*;

@Mixin(SpongeBlock.class)
public class SpongeMixin {
    @Shadow private static final Direction[] DIRECTIONS = Direction.values();

    /**
     * @author Wheeler-Shigley
     * @reason Allow Sponges to absorb all liquids
     */
    @Overwrite
    private boolean absorbWater(World world, BlockPos pos) {
        if( !(world instanceof ServerWorld) ) {
            return false;
        }

        GameRules gameRules = ( (ServerWorld)world ).getGameRules();
        final int       DEPTH           = gameRules.getValue(SPONGE_DEPTH           );
        final boolean   WATER           = gameRules.getValue(SPONGE_WATER           );
        final boolean   LAVA            = gameRules.getValue(SPONGE_LAVA            );
        final boolean   POWDERED_SNOW   = gameRules.getValue(SPONGE_POWDERED_SNOW   );

        return 1 < BlockPos.iterateRecursively(
            pos,
            DEPTH,
            CenteredOctahedralNumber(DEPTH),
            (currentPos, queuer) -> {
                for(Direction direction : DIRECTIONS) {
                    queuer.accept( currentPos.offset(direction) );
                }
            },
            (currentPos) -> {
                if( currentPos.equals(pos) ) {
                    return BlockPos.IterationState.ACCEPT;
                } else {
                    BlockState blockState = world.getBlockState(currentPos);
                    FluidState fluidState = world.getFluidState(currentPos);
                    Block blockAtPosition = blockState.getBlock();
                    boolean drainable =
                        ( WATER          && blockAtPosition.equals(Blocks.WATER)         )
                        || ( LAVA           && blockAtPosition.equals(Blocks.LAVA)          )
                        || ( POWDERED_SNOW  && blockAtPosition.equals(Blocks.POWDER_SNOW)   )
                    ;
                    if(
                        !fluidState.isIn(FluidTags.WATER)
                        && !blockAtPosition.equals(Blocks.LAVA)
                        && !blockAtPosition.equals(Blocks.POWDER_SNOW)
                    ) {
                        return BlockPos.IterationState.SKIP;
                    } else {
                        Block block = blockState.getBlock();
                        if(drainable) {
                            FluidDrainable fluidDrainable = (FluidDrainable)block;
                            if(
                                !fluidDrainable.tryDrainFluid(
                                    (LivingEntity)null,
                                    world,
                                    currentPos,
                                    blockState
                                ).isEmpty()
                            ) {
                                return BlockPos.IterationState.ACCEPT;
                            }
                        }

                        if(drainable) {
                            world.setBlockState(
                                currentPos,
                                Blocks.AIR.getDefaultState(),
                                3
                            );
                        } else {
                            if(
                                   !blockState.isOf(Blocks.KELP)
                                && !blockState.isOf(Blocks.KELP_PLANT)
                                && !blockState.isOf(Blocks.SEAGRASS)
                                && !blockState.isOf(Blocks.TALL_SEAGRASS)
                            ) {
                                return BlockPos.IterationState.SKIP;
                            }
                            if(WATER) {
                                BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(currentPos) : null;
                                dropStacks(blockState, world, currentPos, blockEntity);
                                world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), 3);
                            } else {
                                return BlockPos.IterationState.SKIP;
                            }
                        }

                        return BlockPos.IterationState.ACCEPT;
                    }
                }
            }
        );
    }
}
