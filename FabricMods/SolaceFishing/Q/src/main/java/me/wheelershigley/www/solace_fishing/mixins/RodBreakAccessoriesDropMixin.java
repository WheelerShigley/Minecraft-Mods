package me.wheelershigley.www.solace_fishing.mixins;

import me.wheelershigley.www.solace_fishing.api.fishing.RodAccessories;
import me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class RodBreakAccessoriesDropMixin {
    @Inject(
        method = "hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V",
        at = @At("HEAD")
    )
    public void hurtAndBreak(
        int amount, LivingEntity owner, EquipmentSlot slot,
        CallbackInfo ci
    ) {
        ItemStack itemStack = owner.getItemBySlot(slot);
        if(itemStack.getDamageValue()+1 < itemStack.getMaxDamage() ) {
            return;
        }

        RodAccessories accessories = RodAccessories.of( itemStack, owner.level() );
        if(accessories == null) {
            return;
        }

        for(ItemStack accessory : accessories.getAccessories() ) {
            boolean drop;
            if(owner instanceof Player player) {
                drop = !player.getInventory().add(accessory);
            } else {
                drop = true;
            }
            if(drop) {
                owner.drop(accessory, true, false);
            }
        }
    }
}
