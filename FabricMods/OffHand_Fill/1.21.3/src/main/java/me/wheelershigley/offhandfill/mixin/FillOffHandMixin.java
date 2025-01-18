package me.wheelershigley.offhandfill.mixin;

import me.wheelershigley.offhandfill.Offhandfill;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.wheelershigley.offhandfill.ItemsHelper.*;

@Mixin(PlayerInventory.class)
public abstract class FillOffHandMixin {

    @Shadow @Final public PlayerEntity player;
    @Shadow @Final public DefaultedList<ItemStack> offHand;

    @Inject(method = "Lnet/minecraft/entity/player/PlayerInventory;addStack(Lnet/minecraft/item/ItemStack;)I", at = @At("HEAD"))
    private void addStack(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        ItemStack offHandItem = player.getOffHandStack();
        if(  ArrayUtils.contains(bundles, offHandItem)  ) {
            //TODO
        }
        if(  ArrayUtils.contains(shulkerBoxes, offHandItem)  ) {
            //TODO
        }

        Offhandfill.LOGGER.info("test");
    }
}
