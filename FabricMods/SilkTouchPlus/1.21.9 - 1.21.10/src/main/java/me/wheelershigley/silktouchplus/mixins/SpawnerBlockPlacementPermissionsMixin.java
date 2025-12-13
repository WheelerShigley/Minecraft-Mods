package me.wheelershigley.silktouchplus.mixins;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class SpawnerBlockPlacementPermissionsMixin {
    /**
     * @author Wheeler-Shigley
     * @reason When in survival, Spawner's nbt-data needs to be set
     */
    @Inject(
        method = "writeNbtToBlockEntity",
        at = @At("HEAD"),
            cancellable = true)
    private static void writeNbtToBlockEntity(World world, PlayerEntity player, BlockPos pos, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if( world.isClient() || player == null) {
            cir.setReturnValue(false);
        }
        TypedEntityData< BlockEntityType<?> > typedEntityData = (TypedEntityData)stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
        if(typedEntityData != null) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity != null) {
                if(
                    stack.getItem().equals(Items.SPAWNER)
                    || stack.getItem().equals(Items.TRIAL_SPAWNER)
                    || stack.getItem().equals(Items.VAULT)
                ) {
                    cir.setReturnValue(
                        typedEntityData.applyToBlockEntity(
                            blockEntity,
                            world.getRegistryManager()
                        )
                    );
                }
            }
        }
    }
}
