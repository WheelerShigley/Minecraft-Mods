package www.wheelershigley.me.configurable_sponges.mixins;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraft.block.Block.dropStacks;
import static www.wheelershigley.me.configurable_sponges.gamerules.GameRuleRegistrator.SPONGE_DEPTH;

@Mixin(SpongeBlock.class)
public class SpongeMixin {
    private static int CenteredOctahedralNumber(int depth) {
        double accumulator = 1.0;

        double _depth = (double)depth;
        accumulator += 8.0/3.0 * _depth;

        _depth *= _depth;
        accumulator += 2.0*_depth;

        _depth *= _depth;
        accumulator += 4.0/3.0 * _depth;

        return (int)Math.round(accumulator);
    }

    @Shadow @Final private static final Direction[] DIRECTIONS = Direction.values();

    /**
     * @author Wheeler-Shigley
     * @reason Allow Sponges to absorb all liquids
     */
    @Overwrite
    private boolean absorbWater(World world, BlockPos pos) {
        MinecraftServer server = world.getServer();
        if(server == null) {
            return false;
        }

        final int DEPTH = world.getServer().getGameRules().getInt(SPONGE_DEPTH);

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
                    if( !fluidState.isIn(FluidTags.WATER) ) {
                        return BlockPos.IterationState.SKIP;
                    } else {
                        Block block = blockState.getBlock();
                        if(block instanceof FluidDrainable) {
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

                        if(blockState.getBlock() instanceof FluidBlock) {
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

                            BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(currentPos) : null;
                            dropStacks(blockState, world, currentPos, blockEntity);
                            world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), 3);
                        }

                        return BlockPos.IterationState.ACCEPT;
                    }
                }
            }
        );
    }
}
