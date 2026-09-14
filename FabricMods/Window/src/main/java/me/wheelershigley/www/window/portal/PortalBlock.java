package me.wheelershigley.www.window.portal;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import me.wheelershigley.www.window.api.PortalDefinition;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.wheelershigley.www.window.portal.PortalBlockEntity.getBlockEntityType;

public class PortalBlock extends BaseEntityBlock implements Portal, PolymerBlock {
    public static final EnumProperty<Direction.Axis> AXIS;
    private static final Map<Direction.Axis, VoxelShape> SHAPES;
    public DyeColor COLOR;

    public PortalBlock(Properties properties, DyeColor color) {
        super(properties);
        this.registerDefaultState(
            ( this.stateDefinition.any() )
                .setValue(AXIS, Direction.Axis.X)
        );
        COLOR = color;
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(this);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, @Nullable PacketContext context) {
        return Blocks.AIR.defaultBlockState();
    }

    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    protected void entityInside(
        final BlockState state, final Level level, final BlockPos pos,
        final Entity entity, final InsideBlockEffectApplier effectApplier,
        final boolean isPrecise
    ) {
        if(entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(
                Component.literal("inPortal")
            );
        }
        if( entity.canUsePortal(false) ) {
            entity.setAsInsidePortal(this, pos);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortalBlockEntity(pos, state, COLOR);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level, BlockState state, BlockEntityType<T> type
    ) {
        return createTickerHelper(
            type,
            getBlockEntityType(COLOR),
            PortalBlockEntity::tick
        );
    }

    @Override
    public @Nullable TeleportTransition getPortalDestination(
        @NonNull ServerLevel currentLevel,
        @NonNull Entity entity,
        @NonNull BlockPos portalEntryPos
    ) {
        if(entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(
                Component.literal("TODO")
            );
        }

        return null;
    }

    protected VoxelShape getShape(
        final BlockState state, final BlockGetter level, final BlockPos pos,
        final CollisionContext context
    ) {
        return SHAPES.get( state.getValue(AXIS) );
    }

    @Override
    protected void neighborChanged(
        final BlockState state, final Level level, final BlockPos pos,
        final Block block, final @Nullable Orientation orientation,
        final boolean movedByPiston
    ) {
        if( !isValid(state, pos, level) ) {
            level.destroyBlock(pos, false);
        }
    }
    private boolean isValid(BlockState state, BlockPos position, Level level) {
        List<Direction> checkDirections = new ArrayList<>(); {
            Direction.Axis axis = state.getValue(AXIS);
            if(axis == Direction.Axis.X || axis == Direction.Axis.Y) {
                checkDirections.add(Direction.NORTH);
                checkDirections.add(Direction.SOUTH);
            }
            if(axis == Direction.Axis.X || axis == Direction.Axis.Z) {
                checkDirections.add(Direction.UP);
                checkDirections.add(Direction.DOWN);
            }
            if(axis == Direction.Axis.Y || axis == Direction.Axis.Z) {
                checkDirections.add(Direction.EAST);
                checkDirections.add(Direction.WEST);
            }
        }

        for(Direction direction : checkDirections) {
            Block self = this.defaultBlockState().getBlock();

            PortalBlockEntity blockEntity = (PortalBlockEntity)level.getBlockEntity(position);
            if(blockEntity == null) {
                return false;
            }
            Block material = blockEntity.getFrame();

            Block other =  level.getBlockState( position.relative(direction) ).getBlock();
            if( !other.equals(self) && !other.equals(material) ) {
                return false;
            }
        }
        return true;
    }

    static {
        AXIS = BlockStateProperties.AXIS;
        SHAPES = Map.of(
            Direction.Axis.X, Block.box(0, 0, 0, 4, 16, 16),
            Direction.Axis.Y, Block.box(0, 0, 0, 16, 4, 16),
            Direction.Axis.Z, Block.box(0, 0, 0, 16, 16, 4)
        );
    }
}
