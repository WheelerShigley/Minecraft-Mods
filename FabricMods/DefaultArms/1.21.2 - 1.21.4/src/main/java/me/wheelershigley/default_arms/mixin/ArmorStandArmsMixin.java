package me.wheelershigley.default_arms.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ArmorStandEntity.class)
public abstract class ArmorStandArmsMixin extends LivingEntity {
    protected ArmorStandArmsMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public void setShowArms(boolean showArms) {}
    @Shadow public boolean shouldShowArms() { return false; }
    @Shadow public boolean isMarker() { return false; }
    @Shadow private boolean equip(PlayerEntity player, EquipmentSlot slot, ItemStack stack, Hand hand) { return false; }
    @Shadow private EquipmentSlot getSlotFromPosition(Vec3d hitPos) { return null; }
    @Shadow private boolean isSlotDisabled(EquipmentSlot slot) { return false; }
    @Shadow public Iterable<ItemStack> getHandItems() {
        return null;
    }
    @Shadow @Final private final DefaultedList<ItemStack> heldItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
    @Inject(method = "Lnet/minecraft/entity/decoration/ArmorStandEntity;<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("TAIL"))
    public void ArmorStandEntity(EntityType<? extends ArmorStandEntity> entityType, World world, CallbackInfo ci) {
        this.setShowArms(true);
    }

    /**
     * @author Wheeler-Shigley
     * @reason I need to Mixin both before and after the vanilla implementation, so it has been overwritten.
     */
    @Overwrite
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        //Remove arms (from ArmorStand with arms)
        if(
               itemStack.getItem() == Items.SHEARS
            && this.shouldShowArms()
        ) {
            this.setShowArms(false);
            //drop arms, as sticks
            /*BlockPos position = this.getBlockPos();
            Block.dropStack(
                player.getWorld(),
                new BlockPos( position.getX(), position.getY() + 1, position.getZ() ),
                new ItemStack(Items.STICK, 2)
            );*/
            //drop held items
            for(int i = 0; i < heldItems.size(); i++) {
                if(heldItems.get(i) != ItemStack.EMPTY) {
                    Block.dropStack(
                        this.getWorld(),
                        this.getBlockPos().up(),
                        heldItems.get(i)
                    );
                    heldItems.set(i, ItemStack.EMPTY);
                }
            }

            itemStack.damage(1, player);
            player.getWorld().playSoundFromEntity(null, (Entity)this, SoundEvents.ENTITY_SNOW_GOLEM_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResult.PASS;
        }

        //Add new arms (to ArmorStand without a rms)
        if(
               itemStack.getItem() == Items.STICK //&& 2 <= itemStack.getCount()
            && !this.shouldShowArms()
        ) {
            this.setShowArms(true);

            //take two sticks
            /*if( !player.isCreative() ) {
                itemStack.setCount(itemStack.getCount() - 2);
            }*/

            player.getWorld().playSoundFromEntity(null, (Entity)this, SoundEvents.ENTITY_ITEM_FRAME_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResult.FAIL;
        }

        /*Vanilla Implementation*/ {
            if (!this.isMarker() && !itemStack.isOf(Items.NAME_TAG)) {
                if( player.isSpectator() ) {
                    return ActionResult.SUCCESS;
                } else if( player.getWorld().isClient ) {
                    return ActionResult.SUCCESS_SERVER;
                } else {
                    EquipmentSlot equipmentSlot = this.getPreferredEquipmentSlot(itemStack);
                    if( itemStack.isEmpty() ) {
                        EquipmentSlot equipmentSlot2 = this.getSlotFromPosition(hitPos);
                        EquipmentSlot equipmentSlot3 = this.isSlotDisabled(equipmentSlot2) ? equipmentSlot : equipmentSlot2;
                        if(
                               this.hasStackEquipped(equipmentSlot3)
                            && this.equip(player, equipmentSlot3, itemStack, hand)
                        ) {
                            return ActionResult.SUCCESS_SERVER;
                        }
                    } else {
                        if( this.isSlotDisabled(equipmentSlot) ) {
                            return ActionResult.FAIL;
                        }

                        if(
                               equipmentSlot.getType() == EquipmentSlot.Type.HAND
                            && !this.shouldShowArms()
                        ) {
                            return ActionResult.FAIL;
                        }

                        if( this.equip(player, equipmentSlot, itemStack, hand) ) {
                            return ActionResult.SUCCESS_SERVER;
                        }
                    }

                    return ActionResult.PASS;
                }
            } else {
                return ActionResult.PASS;
            }
        }
    }
}
