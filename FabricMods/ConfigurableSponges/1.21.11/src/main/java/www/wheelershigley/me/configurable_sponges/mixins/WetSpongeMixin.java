package www.wheelershigley.me.configurable_sponges.mixins;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.rule.GameRules;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static www.wheelershigley.me.configurable_sponges.utils.MathFunctions.CenteredOctahedralNumber;
import static www.wheelershigley.me.configurable_sponges.gamerules.GameRuleRegistrator.*;

@Mixin(WetSpongeBlock.class)
public abstract class WetSpongeMixin extends Block {
    public WetSpongeMixin(Settings settings) {
        super(settings);
    }

    private static final Direction[] DIRECTIONS = Direction.values();

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        this.update(world, pos);
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
    }

    @Inject(
        method = "onBlockAdded",
        at = @At("TAIL")
    )
    private void onBlockAdded(
        BlockState state,
        World world,
        BlockPos pos,
        BlockState oldState,
        boolean notify,
        CallbackInfo ci
    ) {
        if(  !oldState.isOf( state.getBlock() )  ) {
            this.update(world, pos);
        }
    }

    protected void update(World world, BlockPos pos) {
        if( this.absorbLiquid(world, pos) ) {
            world.setBlockState(pos, Blocks.SPONGE.getDefaultState(), Block.NOTIFY_LISTENERS);
            world.playSound(null, pos, SoundEvents.BLOCK_SPONGE_ABSORB, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    private boolean absorbLiquid(World world, BlockPos pos) {
        if( !(world instanceof ServerWorld) ) {
            return false;
        }

        GameRules gameRules = ( (ServerWorld)world ).getGameRules();
        final int       DEPTH           = gameRules.getValue(SPONGE_DEPTH               );
        final boolean   WATER           = gameRules.getValue(WET_SPONGE_WATER           );
        final boolean   LAVA            = gameRules.getValue(WET_SPONGE_LAVA            );
        final boolean   POWDERED_SNOW   = gameRules.getValue(WET_SPONGE_POWDERED_SNOW   );

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