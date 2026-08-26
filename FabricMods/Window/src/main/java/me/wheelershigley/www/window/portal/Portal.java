package me.wheelershigley.www.window.portal;

import com.mojang.datafixers.util.Pair;
import me.wheelershigley.www.window.registrations.WindowBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Portal {
    public static final int MIN_WIDTH = 2;

    public static TeleportTransition getTransition(ServerPlayer player, ServerLevel toDimension) {
        if(    toDimension == null
            || player.level().equals(toDimension)
        ){
            return null;
        }
        BlockState frameState = getFrameBlock(toDimension);

        double teleportationScale = DimensionType.getTeleportationScale(
            player.level().dimensionType(),
            toDimension.dimensionType()
        );

        BlockPos approximateExitPos = toDimension.getWorldBorder().clampToBounds(
            player.getX() * teleportationScale,
            player.getY(),
            player.getZ() * teleportationScale
        );

        PortalForcer customPortalForcer = new PortalForcer(toDimension);
        Optional<BlockPos> exitPortalPos = customPortalForcer.findClosestPortalPosition(
            approximateExitPos,
            teleportationScale,
            toDimension.getWorldBorder()
        );

        BlockPos exitPosition;
        if( exitPortalPos.isPresent() ) {
            exitPosition = exitPortalPos.get();
        } else {
             Optional<BlockUtil.FoundRectangle> createdExit = customPortalForcer.createPortal(
                 approximateExitPos,
                 frameState,
                 Direction.Axis.X
             );

            if( createdExit.isEmpty() ) {
                return null;
            }

            Optional<BlockPos> potentialExit = customPortalForcer.findClosestPortalPosition(
                approximateExitPos,
                teleportationScale,
                toDimension.getWorldBorder()
            );
            if( potentialExit.isEmpty() ) {
                return null;
            }
            exitPosition = potentialExit.get();
        }
        Vec3 newPosition = new Vec3( exitPosition.getX(), exitPosition.getY(), exitPosition.getZ() );

        return new TeleportTransition(
            toDimension,
            newPosition,
            player.getDeltaMovement(),
            player.getYRot(),
            player.getXRot(),
            TeleportTransition.PLAY_PORTAL_SOUND
        );
    }

    //TODO
    private static @Nullable BlockState getFrameBlock(ServerLevel toDimension) {
        return Blocks.STONE.defaultBlockState();
    }

    public static boolean attemptPortal(
        Level level, BlockPos position,
        Block frameMaterial, Block ignitionMaterial
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

        boolean worked = false;
        if(x_axis && y_axis) {
            worked = attemptFramePlane(
                level, position,
                Pair.of(Direction.Axis.X, Direction.Axis.Y),
                frameMaterial, ignitionMaterial,
                WindowBlocks.PORTAL.defaultBlockState().setValue(PortalBlock.AXIS, Direction.Axis.X)
            );
        }
        if(!worked && y_axis && z_axis) {
            worked = attemptFramePlane(
                level, position,
                Pair.of(Direction.Axis.Y, Direction.Axis.Z),
                frameMaterial, ignitionMaterial,
                WindowBlocks.PORTAL.defaultBlockState().setValue(PortalBlock.AXIS, Direction.Axis.Z)
            );
        }
        if(!worked && x_axis && z_axis) {
            worked = attemptFramePlane(
                level, position,
                Pair.of(Direction.Axis.X, Direction.Axis.Z),
                frameMaterial, ignitionMaterial,
                WindowBlocks.PORTAL.defaultBlockState().setValue(PortalBlock.AXIS, Direction.Axis.Y)
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
        for(int i = 1; i < innerBounds.x; i++) {
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
        for(int j = 1; j < innerBounds.y; j++) {
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
                level.setBlock(position, portalBlockState, Block.UPDATE_ALL);

                PortalBlockEntity blockEntity = (PortalBlockEntity)level.getBlockEntity(position);
                if(blockEntity != null) {
                    blockEntity.setFrame(  frameMaterial   );
                    blockEntity.setIgniter(ignitionMaterial);
                }
            }
        }
        return true;
    }
}
