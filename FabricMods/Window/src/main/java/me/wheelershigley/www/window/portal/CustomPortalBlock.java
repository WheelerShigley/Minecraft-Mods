package me.wheelershigley.www.window.portal;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
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

import static me.wheelershigley.www.window.Window.getWindowIdentifier;

//TODO Colorations
public class CustomPortalBlock extends Block implements Portal, PolymerTexturedBlock {
    public static final EnumProperty<Direction.Axis> AXIS;
    private static final Map<Direction.Axis, VoxelShape> SHAPES;
    //public static final EnumProperty<DyeColor> COLOR;

    public CustomPortalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
            ( this.stateDefinition.any() )
                .setValue(AXIS, Direction.Axis.X)
                //.setValue(COLOR, DyeColor.WHITE)
        );
    }

    private static final BlockState POLYMER_PORTAL_X = PolymerBlockResourceUtils.requestBlock(
        BlockModelType.TRIPWIRE_FLAT,
        PolymerBlockModel.of( getWindowIdentifier("block/custom_portal_x") )
    );
    private static final BlockState POLYMER_PORTAL_Y = PolymerBlockResourceUtils.requestBlock(
        BlockModelType.TRIPWIRE_FLAT,
        PolymerBlockModel.of( getWindowIdentifier("block/custom_portal_y") )
    );
    private static final BlockState POLYMER_PORTAL_Z = PolymerBlockResourceUtils.requestBlock(
        BlockModelType.TRIPWIRE_FLAT,
        PolymerBlockModel.of( getWindowIdentifier("block/custom_portal_z") )
    );
    @Override
    public BlockState getPolymerBlockState(BlockState state, @Nullable PacketContext context) {
        return switch( state.getValue(AXIS) ) {
            case X -> POLYMER_PORTAL_X;
            case Y -> POLYMER_PORTAL_Y;
            case Z -> POLYMER_PORTAL_Z;
        };
    }

    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS/*, COLOR*/);
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
        //COLOR = EnumProperty.create("color", DyeColor.class);
    }
}
