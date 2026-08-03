package me.wheelershigley.www.silktouchplus.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public class DataPlacePermissionMixin {
    @ModifyExpressionValue(
        method = "updateCustomBlockEntityTag(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/BlockEntityType;onlyOpCanSetNbt()Z"
        )
    )
    private static boolean alwaysAllowedOpForSpawners(
        boolean original,
        @Local ItemStack itemStack
    ) {
        if(  isAlwaysAllowed( itemStack.getItem() )  ) {
            return true;
        }
        return original;
    }

    @ModifyExpressionValue(
        method = "updateCustomBlockEntityTag(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;canUseGameMasterBlocks()Z"
        )
    )
    private static boolean alwaysAllowedMasterBlocksForSpawners(
        boolean original,
        @Local ItemStack itemStack
    ) {
        if(  isAlwaysAllowed( itemStack.getItem() )  ) {
            return true;
        }
        return original;
    }

    @Unique
    private static boolean isAlwaysAllowed(Item item) {
        return (
               item == Items.SPAWNER
            || item == Items.TRIAL_SPAWNER
        );
    }
}
