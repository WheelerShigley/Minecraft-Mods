package me.wheelershigley.www.solace_fishing.implementations;

import eu.pb4.polymer.core.api.item.PolymerItem;
import me.wheelershigley.www.solace_fishing.data.RodAccessories;
import me.wheelershigley.www.solace_fishing.registrations.CustomComponents;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;

import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.isFishingAccessory;
import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.isFishingRod;

public class CustomFishingRod extends FishingRodItem implements PolymerItem {
    public static final Item.Properties DEFAULT_PROPERTIES = new Item.Properties()
        .stacksTo(1)
        .durability(64)
        .enchantable(1)
        .component( CustomComponents.STORED_ITEMS, new HashMap<>() )
    ;

    public CustomFishingRod(Properties properties) {
        super(properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.FISHING_ROD;
    }

    /* Fishing Features */

    @Override
    public @NonNull InteractionResult use(
        final @NonNull Level level,
        final @NonNull Player player,
        final @NonNull InteractionHand hand
    ) {
        if(player.fishing != null) {
            retrieveCast(level, player, hand);
        } else {
            summonCast(level, player, hand);
        }

        return InteractionResult.SUCCESS ;
    }

    @Unique
    private void retrieveCast(Level level, Player player, InteractionHand hand) {
        assert player.fishing != null;
        ItemStack heldStack = player.getItemInHand(hand);

        if( !level.isClientSide() ) {
            int damage = player.fishing.retrieve(heldStack);
            heldStack.hurtAndBreak(
                damage,
                player,
                hand.asEquipmentSlot()
            );
        }

        level.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL,
            1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        heldStack.causeUseVibration(player, GameEvent.ITEM_INTERACT_FINISH);
    }

    @Unique
    private void summonCast(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        //TODO: Use Accessories
        RodAccessories.get(itemStack);

        level.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL,
            0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        if(level instanceof ServerLevel serverLevel) {
            int lureSpeed = (int)(EnchantmentHelper.getFishingTimeReduction(serverLevel, itemStack, player) * 20.0F);
            int luck = EnchantmentHelper.getFishingLuckBonus(serverLevel, itemStack, player);

            FishingHook hook = new FishingHook(player, level, luck, lureSpeed);
            Projectile.spawnProjectile(hook, serverLevel, itemStack);
        }

        player.awardStat( Stats.ITEM_USED.get(this) );
        itemStack.causeUseVibration(player, GameEvent.ITEM_INTERACT_START);
    }

    /* Bundle-like Features */

    @Override
    public boolean overrideStackedOnOther(
        @NonNull ItemStack self, final @NonNull Slot slot,
        final @NonNull ClickAction clickAction, final @NonNull Player player
    ) {
        player.sendSystemMessage(
            Component.literal("A: stacked on other, as " + clickAction.name() )
        );
        ItemStack other = slot.getItem();
        boolean selfIsRod  = isFishingRod(  self.getItem() );
        boolean otherIsRod = isFishingRod( other.getItem() );

        //Neither are rod, do nothing
        if( !selfIsRod && !otherIsRod ) {
            return false;
        }

        ItemStack rod, potentialAccessory;
        if(selfIsRod) {
            rod = self;
            potentialAccessory = other;
        } else {
            rod = other;
            potentialAccessory = self;
        }

        RodAccessories accessories = RodAccessories.get(rod);
        if(accessories == null) {
            return false;
        }
        boolean hasInstance = accessories.hasAnInstanceOf( potentialAccessory.getItem() );

        // Simple Placement/Swap (same as nothing)
        if( selfIsRod && other.isEmpty() ) {
            //Drop item
            if( !accessories.isEmpty() && clickAction.equals(ClickAction.SECONDARY) ) {
                other = accessories.pop();
                accessories.set(rod);
                slot.set(other);
                return true;
            }

            return false;
        }
        if(  !isFishingAccessory( other.getItem() )  ) {
            return false;
        }

        // Swap Accessory
        if(!hasInstance) {
            ItemStack swappedStack = accessories.attemptSwap(other);
            accessories.set(self);
            other = swappedStack;
        }

        // Absorption + Placement
        if(hasInstance) {
            accessories.attemptSwap(other);
            accessories.set(self);
            other = self;
            self = ItemStack.EMPTY;

            player.containerMenu.setCarried(self);
        }

        if( slot.getItem() != other ) {
            slot.set(other);
        }

        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(
        final @NonNull ItemStack self, @NonNull ItemStack other,
        final @NonNull Slot slot, final @NonNull ClickAction clickAction,
        final @NonNull Player player, final @NonNull SlotAccess carriedItem
    ) {
        player.sendSystemMessage(
                Component.literal("B: stacked on me")
        );

        if(
            player.level().isClientSide()
            || self.isEmpty()
            || carriedItem.get().isEmpty()
        ) {
            return false;
        }

        RodAccessories accessories = RodAccessories.get(self);
        if (accessories == null) {
            return false;
        }

        ItemStack swapped = accessories.attemptSwap(other);
        accessories.set(self);
        carriedItem.set(swapped); //broken?

        return true;
    }
}
