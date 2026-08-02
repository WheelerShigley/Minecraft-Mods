package me.wheelershigley.www.default_arms.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorStandItem.class)
public class PlaceArmlessArmorStandMixin {
    @ModifyExpressionValue(
        method = "useOn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/server/level/ServerLevel;Ljava/util/function/Consumer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntitySpawnReason;ZZ)Lnet/minecraft/world/entity/Entity;"
        )
    )
    private Entity modifyArmorStand(
        Entity entity,
        UseOnContext context
    ) {
        boolean showArms = false; {
            ItemStack item = context.getItemInHand();
            if( !item.has(DataComponents.CUSTOM_DATA) ) {
                showArms = true;
            }

            CustomData customData = item.get(DataComponents.CUSTOM_DATA);
            if(customData == null) {
                showArms = true;
            }

            if(!showArms) {
                CompoundTag tag = customData.copyTag();
                showArms = tag.getBooleanOr("showArms", true);
            }
        }

        if(showArms && entity instanceof ArmorStand armorStand) {
            armorStand.setShowArms(true);
        }
        return entity;
    }
}
