package me.wheelershigley.silktouchplus.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.enums.VaultState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.Property;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(BlockItem.class)
public class SpawnerBlockPlacementPermissionsMixin {
    @Shadow @Final @Deprecated private Block block;

    /**
     * @author Wheeler-Shigley
     * @reason When in survival, Spawner's nbt-data needs to be set
     */
    @Inject(
        method = "writeNbtToBlockEntity",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void writeNbtToBlockEntity(
        World world,
        PlayerEntity player,
        BlockPos pos,
        ItemStack stack,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if( world.isClient() || player == null) {
            cir.setReturnValue(false);
        }

        TypedEntityData< BlockEntityType<?> > typedEntityData = (TypedEntityData< BlockEntityType<?> >)stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
        if(typedEntityData == null) {
            cir.setReturnValue(false);
        }
        assert typedEntityData != null;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity == null) {
            cir.setReturnValue(false);
        }
        assert blockEntity != null;

        if(
            stack.getItem().equals(Items.SPAWNER)
            || stack.getItem().equals(Items.TRIAL_SPAWNER)
            || stack.getItem().equals(Items.VAULT)
        ) {
            //TODO: set Ominous Vaults to Ominous
            cir.setReturnValue(
                typedEntityData.applyToBlockEntity(
                    blockEntity,
                    world.getRegistryManager()
                )
            );
        }
    }
}
