package me.wheelershigley.www.window.portal;

import com.mojang.datafixers.util.Pair;
import me.wheelershigley.www.window.api.CustomPoiTypes;
import me.wheelershigley.www.window.api.LevelHelper;
import me.wheelershigley.www.window.api.LinkType;
import me.wheelershigley.www.window.api.PortalDefinition;
import me.wheelershigley.www.window.registrations.WindowBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CustomPortal {
    public static final int MIN_WIDTH = 2;

    @Deprecated
    public static @Nullable TeleportTransition getTransition(
            ServerPlayer player, ServerLevel level
    ) {
        if( level == null
            || player.level().equals(level)
        ){
            return null;
        }

        BlockPos exitPortalPos = getExitLocation(player, level, false, null);
        if(exitPortalPos == null) {
            return null;
        }

        return new TeleportTransition(
            level,
            new Vec3( exitPortalPos.getX(), exitPortalPos.getY(), exitPortalPos.getZ() ),
            player.getDeltaMovement(),
            player.getYRot(),
            player.getXRot(),
            TeleportTransition.PLAY_PORTAL_SOUND
        );
    }
    public static @Nullable TeleportTransition getTransition(
        ServerPlayer player, PortalDefinition definition
    ) {
        ServerLevel fromDimension = player.level().getServer().getLevel( definition.fromDimension() );
        ServerLevel toDimension = player.level().getServer().getLevel( definition.toDimension() );
        if(
            definition.type().equals(LinkType.BIDIRECTIONAL)
            && player.level().equals(toDimension)
        ) {
            //Swap
            ServerLevel temporary = toDimension;
            toDimension = fromDimension;
            fromDimension = temporary;
        }

        if(    toDimension == null
            || player.level().equals(toDimension)
        ){
            return null;
        }

        BlockPos exitPortalPos = getExitLocation(player, toDimension, false, definition);
        if(exitPortalPos == null) {
            exitPortalPos = getExitLocation(player, toDimension, true, definition);
        }
        if(exitPortalPos == null) {
            return null;
        }
        Vec3 newPosition = new Vec3(
            exitPortalPos.getX() + 0.5,
            exitPortalPos.getY() + 0.0,
            exitPortalPos.getZ() + 0.5
        );

        player.setPortalCooldown();
        return new TeleportTransition(
            toDimension,
            newPosition,
            player.getDeltaMovement(),
            player.getYRot(),
            player.getXRot(),
            TeleportTransition.PLAY_PORTAL_SOUND
        );
    }
    private static @Nullable BlockPos getExitLocation(
        ServerPlayer player, ServerLevel toDimension,
        boolean forcePortal, @Nullable PortalDefinition definition
    ) {
        if(definition == null) {
            return null;
        }

        double teleportationScale = definition.scale();
        if(  definition.fromDimension().equals( toDimension.dimension() )  ) {
            teleportationScale = 1.0/teleportationScale;
        }

        if(definition.type() == null) {
            return null;
        }
        BlockPos approximateExitPos;
        if( definition.type().equals(LinkType.MONODIRECTIONAL_RTP) ) {
            approximateExitPos = LevelHelper.getSafeRTPLocation(toDimension);
        } else {
            approximateExitPos = toDimension.getWorldBorder().clampToBounds(
                player.getX() * teleportationScale,
                player.getY(),
                player.getZ() * teleportationScale
            );
        }

        BlockPos position = findClosestPortalPosition(
            approximateExitPos,
            definition,
            definition.color(),
            (int)(16*teleportationScale + 16),
            toDimension
        ).orElse(null);

        if(position == null && forcePortal) {
            PortalForcer customPortalForcer = new PortalForcer(toDimension);
            Optional<BlockUtil.FoundRectangle> createdExit = customPortalForcer.createPortal(
                approximateExitPos,
                definition
            );

            if( createdExit.isEmpty() ) {
                return null;
            } else {
                position = createdExit.get().minCorner;
            }
        }
        if(position == null) {
            return null;
        }

        /* Get Lowest Block in Portal */
        if(definition.color() == null) {
            return null;
        }
        Block coloredPortalBlock = WindowBlocks.coloredPortals.get( definition.color() ).defaultBlockState().getBlock();
        Block currentBlock = coloredPortalBlock;
        while( currentBlock.equals(coloredPortalBlock) ) {
            position = position.below();
            currentBlock = toDimension.getBlockState(position).getBlock();
        }

        return position.above();
    }

    public static Optional<BlockPos> findClosestPortalPosition(
        final BlockPos approximateExitPos,
        final PortalDefinition definition,
        final DyeColor color,
        final int radius,
        final ServerLevel level
    ) {
        PoiManager poiManager = level.getPoiManager();

        poiManager.ensureLoadedAndValid(
            level,
            approximateExitPos,
            radius
        );

        ResourceKey<PoiType> POIType = CustomPoiTypes.portalPOIs.get(color);
        return poiManager
            .getInSquare(
                type -> type.is(POIType),
                approximateExitPos,
                radius,
                PoiManager.Occupancy.ANY
            )
            .map(PoiRecord::getPos)
            .filter(level.getWorldBorder()::isWithinBounds)
            .filter(
                (pos) -> {
                    BlockState state = level.getBlockState(pos);
                    PortalBlockEntity portalBlockEntity = (PortalBlockEntity)level.getBlockEntity(pos);

                    if(    !(state.getBlock() instanceof PortalBlock portalBlock)
                        || !(portalBlockEntity instanceof PortalBlockEntity)
                        || portalBlock.COLOR != color
                    ) {
                        return false;
                    }

                    return definition.frameMaterial().equals(    portalBlockEntity.getFrame()   )
                        && definition.ignitionMaterial().equals( portalBlockEntity.getIgniter() )
                    ;
                }
            )
            .min(
                Comparator.comparingDouble(
                    pos -> pos.distSqr(approximateExitPos)
                )
            )
        ;
    }

    public static boolean attemptPortal(
        Level level, BlockPos position,
        Block frameMaterial, Block ignitionMaterial,
        DyeColor color
    ) {
        List<Direction> directions = getMaterialDirections(level, position, frameMaterial);
        if( directions.isEmpty() ) {
            return false;
        }

        Map<Direction, Optional<Integer> > maximumBounds = getMaximumBounds(level, position, frameMaterial, ignitionMaterial);
        Map<Direction.Axis, Boolean> validAxises = getAxialValidity(maximumBounds);

        boolean x_axis = validAxises.getOrDefault(Direction.Axis.X, Boolean.FALSE);
        boolean y_axis = validAxises.getOrDefault(Direction.Axis.Y, Boolean.FALSE);
        boolean z_axis = validAxises.getOrDefault(Direction.Axis.Z, Boolean.FALSE);

        BlockState state = WindowBlocks.coloredPortals.get(color).defaultBlockState();
        boolean worked = false;
        if(x_axis && y_axis) {
            worked = attemptFramePlane(
                level, position,
                Pair.of(Direction.Axis.X, Direction.Axis.Y),
                frameMaterial, ignitionMaterial,
                state.setValue(PortalBlock.AXIS, Direction.Axis.Z)
            );
        }
        if(!worked && y_axis && z_axis) {
            worked = attemptFramePlane(
                level, position,
                Pair.of(Direction.Axis.Y, Direction.Axis.Z),
                frameMaterial, ignitionMaterial,
                state.setValue(PortalBlock.AXIS, Direction.Axis.X)
            );
        }
        if(!worked && x_axis && z_axis) {
            worked = attemptFramePlane(
                level, position,
                Pair.of(Direction.Axis.X, Direction.Axis.Z),
                frameMaterial, ignitionMaterial,
                state.setValue(PortalBlock.AXIS, Direction.Axis.Y)
            );
        }
        return worked;
    }
    private static List<Direction> getMaterialDirections(Level level, BlockPos position, Block material) {
        List<Direction> directions = new ArrayList<>();

        Block current;
        for(Direction direction : Direction.values() ) {
            current = level.getBlockState( position.relative(direction, 1) ).getBlock();
            if( current.equals(material) ) {
                directions.add(direction);
            }
        }

        return directions;
    }
    private static Map<Direction, Optional<Integer> > getMaximumBounds(
        Level level, BlockPos position,
        Block material, Block ignitionMaterial
    ) {
        Map<Direction, Optional<Integer> > maximumBounds = new HashMap<>();

        for(Direction direction : Direction.values() ) {
            int accumulator = 0;
            Block current;
            int i;
            for(i = 0; i < PortalShape.MAX_WIDTH; i++) {
                current = level.getBlockState( position.relative(direction, i) ).getBlock();
                if( !current.equals(Blocks.AIR) && !current.equals(ignitionMaterial) ) {
                    break;
                }
                accumulator++;
            }
            maximumBounds.put(
                direction,
                level.getBlockState( position.relative(direction, i) ).getBlock().equals(material) ? Optional.of(i-1) : Optional.empty()
            );
        }

        return maximumBounds;
    }
    private static Map<Direction.Axis, Boolean> getAxialValidity(Map<Direction, Optional<Integer> > bounds) {
        Map<Direction.Axis, Boolean> validity = new HashMap<>();

        Map<Direction.Axis, Pair<Direction, Direction> > axisDirectionMap = new HashMap<>();
        axisDirectionMap.put( Direction.Axis.X, Pair.of(Direction.EAST,  Direction.WEST ) );
        axisDirectionMap.put( Direction.Axis.Y, Pair.of(Direction.UP,    Direction.DOWN ) );
        axisDirectionMap.put( Direction.Axis.Z, Pair.of(Direction.SOUTH, Direction.NORTH) );

        for(Map.Entry<Direction.Axis, Pair<Direction, Direction> > axisDirection : axisDirectionMap.entrySet() ) {
            Direction first = axisDirection.getValue().getFirst();
            Direction second = axisDirection.getValue().getSecond();

            validity.put(
                axisDirection.getKey(),
                bounds.containsKey(first) && bounds.get(first).isPresent()
                && bounds.containsKey(second) && bounds.get(second).isPresent()
                && bounds.get(first).get() + bounds.get(second).get() <= PortalShape.MAX_WIDTH
            );
        }

        return validity;
    }
    private static boolean attemptFramePlane(
        Level level, BlockPos datum,
        Pair<Direction.Axis, Direction.Axis> plane,
        Block frameMaterial, Block ignitionMaterial,
        BlockState portalBlockState
    ) {
        //Find a corner (+,+)
        BlockPos corner; {
            Block current;

            int corner_delta_first = 0;
            for(int i = 0; i < PortalShape.MAX_WIDTH; i++) {
                current = level.getBlockState( datum.relative(plane.getFirst(), i) ).getBlock();
                if( current.equals(frameMaterial) ) {
                    break;
                }
                if( !current.equals(ignitionMaterial) && !current.equals(Blocks.AIR) ) {
                    return false;
                }
                corner_delta_first++;
            }

            BlockPos newDatum = datum.relative(plane.getFirst(), corner_delta_first-1);
            int corner_delta_second = 0;
            for(int i = 0; i < PortalShape.MAX_WIDTH; i++) {
                current = level.getBlockState( newDatum.relative(plane.getSecond(), i) ).getBlock();
                if( current.equals(frameMaterial) ) {
                    break;
                }
                if( !current.equals(ignitionMaterial) && !current.equals(Blocks.AIR) ) {
                    return false;
                }
                corner_delta_second++;
            }

            corner = newDatum.relative(plane.getSecond(), corner_delta_second-1);
        }

        // ensure the region is air/ignition [(+,+) to (-,-)]
        int  first_bound = PortalShape.MAX_WIDTH;
        int second_bound = PortalShape.MAX_WIDTH;
        Vec2 innerBounds; {
            Block current;
            for(int i = 0; i < first_bound; i++) {
                current = level.getBlockState( corner.relative(plane.getFirst(), -i) ).getBlock();
                if( current.equals(frameMaterial) ) {
                    first_bound = i;
                    break;
                }
                if( current.equals(ignitionMaterial) || current.equals(Blocks.AIR) ) {
                    continue;
                }
                return false;
            }
            if(first_bound < MIN_WIDTH) {
                return false;
            }

            boolean shouldBreak = false;
            for(int i = 0; i < first_bound; i++) {
                if(shouldBreak) {
                    break;
                }
                for(int j = 1; j < second_bound; j++) {
                    current = level.getBlockState(
                        corner.relative(plane.getFirst(), -i).relative(plane.getSecond(), -j)
                    ).getBlock();
                    if( current.equals(frameMaterial) ) {
                        second_bound = j;
                        shouldBreak = true;
                        break;
                    }
                    if( current.equals(ignitionMaterial) || current.equals(Blocks.AIR) ) {
                        continue;
                    }
                    return false;
                }
            }
            if(second_bound < MIN_WIDTH) {
                return false;
            }

            innerBounds = new Vec2(first_bound, second_bound);
        }

        // check that the frame is valid
        BlockPos frameDatum = corner.relative(plane.getFirst(), 1).relative(plane.getSecond(), 1);
        Block current;
        for(int i = 1; i <= innerBounds.x; i++) {
            current = level.getBlockState( frameDatum.relative(plane.getFirst(), -i) ).getBlock();
            if( !current.equals(frameMaterial) ) {
                return false;
            }
            current = level.getBlockState(
                frameDatum.relative(plane.getFirst(), -i).relative(plane.getSecond(), -(int)innerBounds.y-1)
            ).getBlock();
            if( !current.equals(frameMaterial) ) {
                return false;
            }
        }
        for(int j = 1; j <= innerBounds.y; j++) {
            current = level.getBlockState( frameDatum.relative(plane.getSecond(), -j) ).getBlock();
            if( !current.equals(frameMaterial) ) {
                return false;
            }
            current = level.getBlockState(
                frameDatum.relative(plane.getSecond(), -j).relative(plane.getFirst(), -(int)innerBounds.x-1)
            ).getBlock();
            if( !current.equals(frameMaterial) ) {
                return false;
            }
        }

        // fill the portal-volume
        for(int i = 0; i < innerBounds.x; i++) {
            for(int j = 0; j < innerBounds.y; j++) {
                BlockPos position = corner
                    .relative(plane.getFirst(), -i)
                    .relative(plane.getSecond(), -j)
                ;
                level.setBlock(position, portalBlockState, Block.UPDATE_CLIENTS);
                PortalBlockEntity portalBlockEntity = (PortalBlockEntity)level.getBlockEntity(position);
                if(portalBlockEntity != null) {
                    portalBlockEntity.setFrame(frameMaterial);
                    portalBlockEntity.setIgniter(ignitionMaterial);
                }
            }
        }
        return true;
    }
}
