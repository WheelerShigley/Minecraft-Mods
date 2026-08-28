package me.wheelershigley.www.window.portal;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.block.PolymerBlock;
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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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

    static {
        AXIS = BlockStateProperties.AXIS;
        SHAPES = Map.of(
            Direction.Axis.X, Block.box(0, 0, 0, 4, 16, 16),
            Direction.Axis.Y, Block.box(0, 0, 0, 16, 4, 16),
            Direction.Axis.Z, Block.box(0, 0, 0, 16, 16, 4)
        );
    }
}
