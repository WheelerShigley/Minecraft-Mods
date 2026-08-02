package me.wheelershigley.www.default_arms.mixins;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.wheelershigley.www.default_arms.gamerule.registerGameRule.ARMLESS_ARMOR_STAND_DROPS_WITH_LORE;

@Mixin(ArmorStand.class)
public abstract class ArmorStandArmsMixin extends LivingEntity {
    protected ArmorStandArmsMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Shadow public boolean showArms() { return true; }
    @Shadow public void setShowArms(final boolean value) {}
    @Shadow protected abstract void playBrokenSound();

    /**
     * @author Wheeler-Shigley
     * @reason Custom interactions.
     */
    @Inject(
        method = "interact",
        at = @At("HEAD"),
        cancellable = true
    )
    public void interact(
        Player player, InteractionHand hand,
        Vec3 location,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        if( player.level().isClientSide() ) {
            return;
        }
        ItemStack itemStack = player.getItemInHand(hand);

        //Remove arms (from ArmorStand with arms)
        if(
            itemStack.getItem() == Items.SHEARS
            && this.showArms()
        ) {
            shearArms(player, itemStack);
            cir.setReturnValue(InteractionResult.PASS);
        }

        //Add new arms (to ArmorStand without a rms)
        if(
            itemStack.getItem() == Items.STICK && 2 <= itemStack.getCount()
            && !this.showArms()
        ) {
            addArms(player, itemStack);
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Unique
    private void shearArms(Player player, ItemStack itemStack) {
        //drop arms, as sticks
        BlockPos position = this.blockPosition();
        Block.popResource(
            player.level(),
            new BlockPos( position.getX(), position.getY() + 1, position.getZ() ),
            new ItemStack(Items.STICK, 2)
        );

        //drop held items
        EquipmentSlot[] heldSlots = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
        for(EquipmentSlot slot : heldSlots) {
            ItemStack heldStack = this.equipment.set(slot, ItemStack.EMPTY);
            if( !heldStack.isEmpty() ) {
                Block.popResource(
                    this.level(),
                    this.blockPosition().above(),
                    heldStack
                );
            }
        }

        this.setShowArms(false);
        itemStack.hurtWithoutBreaking(1, player);
        this.playBrokenSound();
    }

    @Unique
    public void addArms(Player player, ItemStack itemStack) {
        this.setShowArms(true);

        //take two sticks
        if( !player.isCreative() ) {
            itemStack.shrink(2);
        }

        player.level().playSound(
            null,
            (Entity)this,
            SoundEvents.ITEM_FRAME_BREAK,
            SoundSource.BLOCKS,
            1.0F,
            1.0F
        );
    }

    /**
     * @author Wheeler-Shigley
     * @reason Drop armless ArmorStands with custom nbt
     */
    @Inject(
        method = "brokenByPlayer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"
        )
    )
    private void modifyItemStack(
        ServerLevel level, DamageSource source,
        CallbackInfo ci,
        @Local ItemStack result
    ) {
        if( !this.showArms() ) {
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean("showArms", false);
            result.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt) );

            if( level.getGameRules().get(ARMLESS_ARMOR_STAND_DROPS_WITH_LORE) ) {
                ItemLore lore = new ItemLore(
                    ImmutableList.of(
                        Component.literal("§r" + Component.translatable("default_arms.text.armless_lore").getString() )
                    )
                );
                result.set(DataComponents.LORE, lore);
            }
        }
    }
}
