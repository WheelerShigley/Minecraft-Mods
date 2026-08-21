package me.wheelershigley.www.window.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;

public class CustomPortal {

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

        CustomPortalForcer customPortalForcer = new CustomPortalForcer(toDimension);
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

    private static BlockState getFrameBlock(ServerLevel toDimension) {
        //TODO
        return Blocks.STONE.defaultBlockState();
    }
}
