package me.wheelershigley.www.solace_fishing.mixins;

import me.wheelershigley.www.solace_fishing.implementations.CustomFishingRod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHook.class)
public abstract class FishHookRodChangesMixin {

    @Shadow
    public abstract @Nullable Player getPlayerOwner();

    @Unique
    private ItemStack fishingRod = ItemStack.EMPTY;

    @Inject(
        method = "<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V",
        at = @At("TAIL")
    )
    public void FishingHook(
        Player player, Level level,
        int luck, int lureSpeed,
        CallbackInfo ci
    ) {
        ItemStack potentialRod = player.getMainHandItem();
        if( potentialRod.isEmpty() ) {
            return;
        }

        if(    potentialRod.getItem() == Items.FISHING_ROD
            || potentialRod.getItem() instanceof CustomFishingRod
        ) {
            this.fishingRod = potentialRod;
        } else {
            this.fishingRod = ItemStack.EMPTY;
        }
    }

    @Inject(
        method = "remove",
        at = @At("HEAD")
    )
    public void retrieve(Entity.RemovalReason reason, CallbackInfo ci) {
        if( this.fishingRod.isEmpty() ) {
            return;
        }

        this.fishingRod.remove(DataComponents.CUSTOM_MODEL_DATA);

        Player owner = this.getPlayerOwner();
        if(owner == null) {
            return;
        }
        Inventory inventory = owner.getInventory();
        for(int index = 0; index < inventory.getContainerSize(); index++) {
            ItemStack stack = inventory.getItem(index);

            if( stack.getItem() == Items.FISHING_ROD
                || stack.getItem() instanceof CustomFishingRod
            ) {
                stack.remove(DataComponents.CUSTOM_MODEL_DATA);
            }
        }

        ItemStack carried = owner.containerMenu.getCarried();
        if(    carried.getItem() == Items.FISHING_ROD
            || carried.getItem() instanceof CustomFishingRod
        ) {
            carried.remove(DataComponents.CUSTOM_MODEL_DATA);
        }
        owner.containerMenu.broadcastFullState();
    }
}
