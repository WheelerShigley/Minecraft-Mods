package me.wheelershigley.www.trampleless.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.wheelershigley.www.trampleless.TramplelessGameRules.*;

@Mixin(FarmlandBlock.class)
public abstract class TramplePreventionMixin extends Block {
    public TramplePreventionMixin(Properties settings) {
        super(settings);
    }

    @Unique
    private static boolean itemHasEnchantment(ItemStack item, ResourceKey<Enchantment> registeredEnchant) {
        for(Holder<Enchantment> enchant : EnchantmentHelper.getEnchantmentsForCrafting(item).keySet() ) {
            if(
                enchant.unwrapKey().isPresent()
                && enchant.unwrapKey().get().equals(registeredEnchant)
            ) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean entityHasFeatherFallingBoots(Entity entity) {
        LivingEntity potentiallyArmoredEntity;
        if(entity instanceof LivingEntity) {
            potentiallyArmoredEntity = (LivingEntity)entity;
        } else {
            return false;
        }

        boolean hasFeatherFallingBoots = false;
        for(EquipmentSlot equipmentSlot : EquipmentSlotGroup.ARMOR) {
            if(equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack armor = potentiallyArmoredEntity.getItemBySlot(equipmentSlot);
                if(
                    !armor.isEmpty()
                    && armor.is(ItemTags.FOOT_ARMOR)
                    && armor.has(DataComponents.ENCHANTMENTS)
                ) {
                    if( itemHasEnchantment(armor, Enchantments.FEATHER_FALLING) ) {
                        hasFeatherFallingBoots = true;
                    }
                }
            }
        }
        return hasFeatherFallingBoots;
    }

    /**
     * @author Wheeler-Shigley
     * @reason Having FeatherFalling will prevent FarmLand-trampling
     */
    @Inject(
        method = "fallOn",
        at = @At("HEAD"),
        cancellable = true
    )
    public void fallOn(
        Level level,
        BlockState state, BlockPos pos,
        Entity entity, double fallDistance,
        CallbackInfo ci
    ) {
        if( !(level instanceof ServerLevel) ) {
            return;
        }

        GameRules gameRules = ( (ServerLevel)level ).getGameRules();
        boolean farmlandTrampling =         gameRules.get(FARMLAND_TRAMPLING);
        boolean featherFallingTrampling =   gameRules.get(FEATHER_FALLING_TRAMPLING);

        if(!farmlandTrampling) {
            ci.cancel();
        }

        boolean hasFeatherFallingBoots = entityHasFeatherFallingBoots(entity);
        if(!featherFallingTrampling && hasFeatherFallingBoots) {
            ci.cancel();
        }
    }
}
