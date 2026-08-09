package me.wheelershigley.www.solace_fishing.mixins;

import me.wheelershigley.www.solace_fishing.implementations.CustomFishingRod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class PlayerLeavingCustomModelFixesMixin {
    @Shadow
    public abstract ServerPlayer getPlayer();

    @Inject(
        method = "removePlayerFromWorld",
        at = @At("HEAD")
    )
    public void removeCustomFishingRodCustomDataModelInformation(CallbackInfo ci) {
        //TODO: rely on the assumption that a cast rod must be being held.

        Inventory inventory = this.getPlayer().getInventory();

        for(int index = 0; index < inventory.getContainerSize(); index++) {
            ItemStack itemStack = inventory.getItem(index);
            if(itemStack.getItem() instanceof CustomFishingRod) {
                itemStack.remove(DataComponents.CUSTOM_MODEL_DATA);
            }
        }
    }

}
