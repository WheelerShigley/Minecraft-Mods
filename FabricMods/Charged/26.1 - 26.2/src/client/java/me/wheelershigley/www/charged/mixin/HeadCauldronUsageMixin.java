package me.wheelershigley.www.charged.mixin;

import me.wheelershigley.www.charged.ChargedClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StandingAndWallBlockItem.class)
public class HeadCauldronUsageMixin {
    @Inject(
        method = "getPlacementState",
        at = @At("TAIL"),
        cancellable = true
    )
    protected void getPlacementState(
        BlockPlaceContext context,
        CallbackInfoReturnable<BlockState> cir
    ) {
        if( ChargedClient.isWashingEnabled && isTargetBlockWaterCauldron(context) ) {
            cir.setReturnValue(null);
        }
    }

    @Unique
    private static boolean isTargetBlockWaterCauldron(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if(player == null) {
            return false;
        }

        ItemStack item = player.getItemInHand( context.getHand() );
        ResolvableProfile profileComponent = item.get(DataComponents.PROFILE);
        if(profileComponent == null) {
            return false;
        }

        BlockState targetedBlock = null; {
            Entity camera = Minecraft.getInstance().getCameraEntity();
            if(camera == null) {
                return false;
            }
            HitResult blockHit = camera.pick(20.0D, 0.0F, false);
            if(blockHit.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ( (BlockHitResult)blockHit ).getBlockPos();
                targetedBlock = context.getLevel().getBlockState(blockPos);
            }
        }
        if(targetedBlock == null) {
            return false;
        }

        return targetedBlock.getBlock().equals(Blocks.WATER_CAULDRON);
    }
}
