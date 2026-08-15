package me.wheelershigley.www.solace_fishing.helpers;

import me.wheelershigley.www.solace_fishing.implementations.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// Based on FishingHook.class
public class MetaFishingHelper {
    private enum OpenWaterType {
        ABOVE_WATER,
        INSIDE_WATER,
        INVALID
    }

    private static OpenWaterType getOpenWaterTypeAt(
        Level level,
        BlockPos pos
    ) {
        BlockState state = level.getBlockState(pos);

        if( state.isAir()
            || state.is(Blocks.LILY_PAD) ) {
            return OpenWaterType.ABOVE_WATER;
        }

        FluidState fluidState = state.getFluidState();

        if (
            fluidState.is(FluidTags.WATER)
            && fluidState.isSource()
            && state.getCollisionShape(level, pos).isEmpty()
        ) {
            return OpenWaterType.INSIDE_WATER;
        }

        return OpenWaterType.INVALID;
    }

    private static OpenWaterType getOpenWaterTypeForArea(
        Level level,
        BlockPos from,
        BlockPos to
    ) {
        return BlockPos.betweenClosedStream(from, to)
            .map(
                pos -> getOpenWaterTypeAt(level, pos)
            )
            .reduce(
                (a, b) -> a == b ? a : OpenWaterType.INVALID
            )
            .orElse(OpenWaterType.INVALID)
        ;
    }

    public static @Nullable ItemStack getFirstFishingRod(Player player) {
        Inventory inventory = player.getInventory();
        final int INVENTORY_SIZE = inventory.getContainerSize();
        final int ROW_LENGTH = 9;
        ItemStack current;

        // Main Hand
        current = player.getMainHandItem();
        if(  isFishingRod( current.getItem() )  ) {
            return current;
        }

        // Offhand
        current = player.getOffhandItem();
        if(  isFishingRod( current.getItem() )  ) {
            return current;
        }

        // Hotbar
        for(int slot = 0; slot < ROW_LENGTH; slot++) {
            current = inventory.getItem(slot+INVENTORY_SIZE);
            if(  isFishingRod( current.getItem() )  ) {
                return current;
            }
        }

        // Inventory (Body)
        for(int slot = 0; slot < INVENTORY_SIZE; slot++) {
            current = inventory.getItem(slot);
            if(  isFishingRod( current.getItem() )  ) {
                return current;
            }
        }

        return null;
    }

    public static int getLuckOfRod(Level level, ItemStack rod) {
        if( !isFishingRod(rod.getItem() ) ) {
            return 0;
        }

        Holder<Enchantment> luckOfTheSea =
            level.registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.LUCK_OF_THE_SEA)
        ;

        return EnchantmentHelper.getItemEnchantmentLevel(luckOfTheSea, rod);
    }

    public static boolean isOpenWater(
            Level level,
            BlockPos blockPos
    ) {
        OpenWaterType previousLayer = OpenWaterType.INVALID;

        for(int y = -1; y <= 2; ++y) {
            OpenWaterType layer = getOpenWaterTypeForArea(
                level,
                blockPos.offset(-2, y, -2),
                blockPos.offset(2, y, 2)
            );

            switch(layer) {
                case ABOVE_WATER:
                    if(previousLayer == OpenWaterType.INVALID) {
                        return false;
                    }
                    break;
                case INSIDE_WATER:
                    if(previousLayer == OpenWaterType.ABOVE_WATER) {
                        return false;
                    }
                    break;
                case INVALID:
                    return false;
            }
            previousLayer = layer;
        }

        return true;
    }

    public static boolean isFishingRod(Item item) {
        return
               item == Items.FISHING_ROD
            || item instanceof CustomFishingRod
        ;
    }

    public static boolean isFishingAccessory(Item item) {
        return (item instanceof AccessoryItem);
    }
}