package me.wheelershigley.www.silktouchplus.mixins;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(VaultBlock.class)
public class OminousVaultPlacementMixin {
    /**
     * @author Wheeler-Shigley
     * @reason Placed Vaults should initialize as Ominous when they require an ominous-key
     */
    @Inject(
        method = "getStateForPlacement",
        at = @At("RETURN"),
        cancellable = true
    )
    public void getStateForPlacement(
        BlockPlaceContext blockPlaceContext,
        CallbackInfoReturnable<BlockState> cir
    ) {
        ItemStack stack = blockPlaceContext.getItemInHand();
        BlockState state = cir.getReturnValue();

        TypedEntityData< BlockEntityType<?> > blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blockEntityData == null) {
            return;
        }
        CompoundTag tag = blockEntityData.copyTagWithoutId();
        Optional<CompoundTag> config = tag.getCompound("config");
        if( config.isEmpty() ) {
            return;
        }
        Optional<CompoundTag> keyItem = config.get().getCompound("key_item");
        if( keyItem.isEmpty() ) {
            return;
        }

        String keyId = keyItem.get().getString("id").orElse("");
        cir.setReturnValue(
            state.setValue(
                VaultBlock.OMINOUS,
                keyId.equals("minecraft:ominous_trial_key")
            )
        );
    }
}
