package me.wheelershigley.www.window.portal;

import me.wheelershigley.www.window.api.CustomPoiTypes;
import me.wheelershigley.www.window.registrations.WindowBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.BlockUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class CustomPortalForcer {

    private final ServerLevel level;
    public CustomPortalForcer(final ServerLevel level) {
        this.level = level;
    }

    public Optional<BlockPos> findClosestPortalPosition(
        final BlockPos approximateExitPos, final double ratio,
        final WorldBorder worldBorder
    ) {
        PoiManager poiManager = this.level.getPoiManager();
        int radius = (int)(16.0/ratio);
        poiManager.ensureLoadedAndValid(this.level, approximateExitPos, radius);
        Stream<BlockPos> checker = poiManager.getInSquare(
            (type) -> type.is(CustomPoiTypes.CUSTOM_PORTAL),
            approximateExitPos,
            radius,
            PoiManager.Occupancy.ANY
        ).map(PoiRecord::getPos);
        Objects.requireNonNull(worldBorder);

        return checker
            .filter(worldBorder::isWithinBounds)
            .filter(
                (pos) -> this.level.getBlockState(pos).hasProperty(BlockStateProperties.AXIS)
            )
            .min(
                Comparator.comparingDouble(
                    (BlockPos p) -> p.distSqr(approximateExitPos)
                )
                .thenComparingInt(Vec3i::getY)
            )
        ;
    }

    public Optional<BlockUtil.FoundRectangle> createPortal(
        final BlockPos origin, final BlockState frameState,
        final Direction.Axis portalAxis
    ) {
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, portalAxis);
        double closestFullDistanceSqr = -1.0;
        BlockPos closestFullPosition = null;
        double closestPartialDistanceSqr = -1.0;
        BlockPos closestPartialPosition = null;
        WorldBorder worldBorder = this.level.getWorldBorder();
        int maxPlaceableY = Math.min(this.level.getMaxY(), this.level.getMinY() + this.level.getLogicalHeight() - 1);
        int edgeDistance = 1;
        BlockPos.MutableBlockPos mutable = origin.mutable();

        for(BlockPos.MutableBlockPos columnPos : BlockPos.spiralAround(origin, 16, Direction.EAST, Direction.SOUTH)) {
            int height = Math.min(
                maxPlaceableY,
                this.level.getHeight( Heightmap.Types.MOTION_BLOCKING, columnPos.getX(), columnPos.getZ() )
            );
            if(    worldBorder.isWithinBounds(columnPos)
                && worldBorder.isWithinBounds( columnPos.move(direction, 1) )
            ) {
                columnPos.move(direction.getOpposite(), 1);

                for(int y = height; this.level.getMinY() <= y; --y) {
                    columnPos.setY(y);
                    if( this.canPortalReplaceBlock(columnPos) ) {
                        int firstEmptyY;
                        for(firstEmptyY = y; this.level.getMinY() < y && this.canPortalReplaceBlock(columnPos.move(Direction.DOWN)); --y) {}

                        if(y + 4 <= maxPlaceableY) {
                            int deltaY = firstEmptyY - y;
                            if(deltaY <= 0 || 3 <= deltaY) {
                                columnPos.setY(y);
                                if( this.canHostFrame(columnPos, mutable, direction, 0) ) {
                                    double distance = origin.distSqr(columnPos);
                                    if(    this.canHostFrame(columnPos, mutable, direction, -1)
                                        && this.canHostFrame(columnPos, mutable, direction, 1)
                                        && (closestFullDistanceSqr == (double)-1.0F || closestFullDistanceSqr > distance)
                                    ) {
                                        closestFullDistanceSqr = distance;
                                        closestFullPosition = columnPos.immutable();
                                    }

                                    if(     closestFullDistanceSqr    == (double)-1.0F
                                        && (closestPartialDistanceSqr == (double)-1.0F || closestPartialDistanceSqr > distance)
                                    ) {
                                        closestPartialDistanceSqr = distance;
                                        closestPartialPosition = columnPos.immutable();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (closestFullDistanceSqr == (double)-1.0F && closestPartialDistanceSqr != (double)-1.0F) {
            closestFullPosition = closestPartialPosition;
            closestFullDistanceSqr = closestPartialDistanceSqr;
        }

        if (closestFullDistanceSqr == (double)-1.0F) {
            int minStartY = Math.max(this.level.getMinY() - -1, 70);
            int maxStartY = maxPlaceableY - 9;
            if (maxStartY < minStartY) {
                return Optional.empty();
            }

            closestFullPosition = (new BlockPos(origin.getX() - direction.getStepX(), Mth.clamp(origin.getY(), minStartY, maxStartY), origin.getZ() - direction.getStepZ() * 1)).immutable();
            closestFullPosition = worldBorder.clampToBounds(closestFullPosition);
            Direction clockWise = direction.getClockWise();

            // PLATFORM
            for(int box = -1; box < 2; ++box) {
                for(int width = 0; width < 2; ++width) {
                    for(int height = -1; height < 3; ++height) {
                        BlockState blockState = height < 0 ? frameState : Blocks.AIR.defaultBlockState();
                        mutable.setWithOffset(closestFullPosition, width * direction.getStepX() + box * clockWise.getStepX(), height, width * direction.getStepZ() + box * clockWise.getStepZ());
                        this.level.setBlockAndUpdate(mutable, blockState);
                    }
                }
            }
        }

        // FRAME
        for(int width = -1; width < 3; ++width) {
            for(int height = -1; height < 4; ++height) {
                if (width == -1 || width == 2 || height == -1 || height == 3) {
                    mutable.setWithOffset(closestFullPosition, width * direction.getStepX(), height, width * direction.getStepZ());
                    this.level.setBlock(mutable, frameState, 3);
                }
            }
        }

        // PORTAL
        Holder<PoiType> poiType = this.level.registryAccess()
            .lookupOrThrow(Registries.POINT_OF_INTEREST_TYPE)
            .getOrThrow(CustomPoiTypes.CUSTOM_PORTAL)
        ;
        PoiManager poiManager = this.level.getPoiManager();
        //TODO: set blockState material
        BlockState portalBlockState = WindowBlocks.CUSTOM_PORTAL_BLOCK.defaultBlockState().setValue(CustomPortalBlock.AXIS, portalAxis);
        for(int width = 0; width < 2; ++width) {
            for(int height = 0; height < 3; ++height) {
                mutable.setWithOffset(closestFullPosition, width * direction.getStepX(), height, width * direction.getStepZ());
                this.level.setBlock(mutable, portalBlockState, 18);
                poiManager.add(mutable.immutable(), poiType);
            }
        }

        return Optional.of(
            new BlockUtil.FoundRectangle(closestFullPosition.immutable(), 2, 3)
        );
    }

    private boolean canPortalReplaceBlock(final BlockPos.MutableBlockPos pos) {
        BlockState blockState = this.level.getBlockState(pos);
        return blockState.canBeReplaced() && blockState.getFluidState().isEmpty();
    }

    private boolean canHostFrame(final BlockPos origin, final BlockPos.MutableBlockPos mutable, final Direction direction, final int offset) {
        Direction clockWise = direction.getClockWise();

        for(int width = -1; width < 3; ++width) {
            for(int height = -1; height < 4; ++height) {
                mutable.setWithOffset(origin, direction.getStepX() * width + clockWise.getStepX() * offset, height, direction.getStepZ() * width + clockWise.getStepZ() * offset);
                if( height < 0 && !this.level.getBlockState(mutable).isSolid() ) {
                    return false;
                }

                if(0 <= height && !this.canPortalReplaceBlock(mutable) ) {
                    return false;
                }
            }
        }

        return true;
    }
}
