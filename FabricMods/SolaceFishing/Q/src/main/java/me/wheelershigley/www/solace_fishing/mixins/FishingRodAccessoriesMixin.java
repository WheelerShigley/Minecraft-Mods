package me.wheelershigley.www.solace_fishing.mixins;

import me.wheelershigley.www.solace_fishing.api.fishing.RodAccessories;
import me.wheelershigley.www.solace_fishing.api.lore.LoreRenderedRodAccessoryComponent;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.isFishingAccessory;
import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.isFishingRod;

@Mixin(Item.class)
public class FishingRodAccessoriesMixin {
    @Inject(
        method = "overrideStackedOnOther",
        at = @At("HEAD"),
        cancellable = true
    )
    public void overrideStackedOnOther(
        ItemStack self, Slot slot,
        ClickAction clickAction, Player player,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if( player.level().isClientSide() ) {
            return;
        }

        ItemStack other = slot.getItem();
        boolean selfIsRod  = isFishingRod(  self.getItem() );
        boolean otherIsRod = isFishingRod( other.getItem() );

        //Neither are rod, do nothing
        if( !selfIsRod && !otherIsRod ) {
            return;
        }
        //Both are rods, swap
        if(selfIsRod && otherIsRod) {
            return;
        }

        ItemStack rod, potentialAccessory;
        if(selfIsRod) {
            rod = self;
            potentialAccessory = other;
        } else {
            rod = other;
            potentialAccessory = self;
        }

        RodAccessories accessories = RodAccessories.of(rod, player.level() );
        LoreRenderedRodAccessoryComponent componentRenderer = new LoreRenderedRodAccessoryComponent(accessories, player.level() );
        if(accessories == null) {
            return;
        }

        // Simple Placement/Swap (same as nothing)
        if( selfIsRod && other.isEmpty() ) {
            if( ClickAction.SECONDARY.equals(clickAction) ) {
                //Drop item into slot
                if( !accessories.isEmpty() ) {
                    ItemStack popped = accessories.pop();
                    componentRenderer.set(rod);
                    slot.set(popped);
                }
                cir.setReturnValue(true);
            }
            //Simple Place
            return;
        }

        if(  !isFishingAccessory( other.getItem() )  ) {
            return;
        }

        boolean hasInstance = accessories.hasAnInstanceOf( potentialAccessory.getItem() );

        // Swap Accessory (Keep carrying Rod)
        if(hasInstance) {
            if( ClickAction.SECONDARY.equals(clickAction) ) {
                return;
            }

            ItemStack swappedStack = accessories.attemptSwap(other);
            componentRenderer.set(self);
            player.containerMenu.setCarried(self);
            slot.set(swappedStack);
        }

        // Absorb Accessory (Keep carrying Rod)
        if(!hasInstance) {
            if( ClickAction.SECONDARY.equals(clickAction) ) {
                return;
            }

            accessories.attemptSwap(other);
            componentRenderer.set(self);

            slot.set(ItemStack.EMPTY);
            player.containerMenu.setCarried(self);
        }

        cir.setReturnValue(true);
    }

    @Inject(
        method = "overrideOtherStackedOnMe",
        at = @At("HEAD"),
        cancellable = true
    )
    public void overrideOtherStackedOnMe(
        ItemStack self, ItemStack other, Slot slot,
        ClickAction clickAction, Player player, SlotAccess carriedItem,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if( player.level().isClientSide() ) {
            return;
        }
        if(
            self.isEmpty()
            || !isFishingRod( self.getItem() )
        ) {
            return;
        }

        RodAccessories accessories = RodAccessories.of(self, player.level() );
        LoreRenderedRodAccessoryComponent componentRenderer = new LoreRenderedRodAccessoryComponent(accessories, player.level() );
        if (accessories == null) {
            return;
        }

        if(  !isFishingAccessory( other.getItem() )  ) {
            if( other.isEmpty() && ClickAction.SECONDARY.equals(clickAction) ) {
                //Do nothing
                cir.setReturnValue(true);
                return;
            }

            //Do not Absorb
            return;
        }

        if( ClickAction.PRIMARY.equals(clickAction) ) {
            boolean swap = accessories.hasAnInstanceOf( other.getItem() );

            //Absorb
            ItemStack swappedStack = accessories.attemptSwap(other);
            componentRenderer.set(self);
            player.containerMenu.setCarried(
                swap ? swappedStack : ItemStack.EMPTY
            );

            cir.setReturnValue(true);
            return;
        }

        //Swap Stacks
        return;
    }
}
