package www.wheelershigley.me.charged.mixin;

import me.wheelershigley.charged.Charged;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import www.wheelershigley.me.charged.ChargedClient;

@Mixin(VerticallyAttachableBlockItem.class)
public class HeadCauldronUsageMixin extends BlockItem {
    public HeadCauldronUsageMixin(Block block, Settings settings) {
        super(block, settings);
    }

    @Inject(
        method = "getPlacementState",
        at = @At("TAIL"),
        cancellable = true
    )
    protected void getPlacementState(
        ItemPlacementContext context,
        CallbackInfoReturnable<BlockState> cir
    ) {
        if( ChargedClient.isWashingEnabled && isTargetBlockWaterCauldron(context) ) {
            cir.setReturnValue(null);
        }
    }

    @Unique
    private static boolean isTargetBlockWaterCauldron(ItemPlacementContext context) {
        PlayerEntity player = context.getPlayer();
        if(player == null) {
            return false;
        }

        ItemStack item = player.getStackInHand( context.getHand() );
        ProfileComponent profileComponent = item.get(DataComponentTypes.PROFILE);
        if(profileComponent == null) {
            return false;
        }

        BlockState targetedBlock = null; {
            Entity camera = MinecraftClient.getInstance().getCameraEntity();
            if(camera == null) {
                return false;
            }
            HitResult blockHit = camera.raycast(20.0D, 0.0F, false);
            if(blockHit.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ( (BlockHitResult)blockHit ).getBlockPos();
                targetedBlock = context.getWorld().getBlockState(blockPos);
            }
        }
        if(targetedBlock == null) {
            return false;
        }

        return targetedBlock.getBlock().equals(Blocks.WATER_CAULDRON);
    }
}
