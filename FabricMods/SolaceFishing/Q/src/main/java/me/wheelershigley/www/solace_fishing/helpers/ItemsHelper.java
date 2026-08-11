package me.wheelershigley.www.solace_fishing.helpers;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

public class ItemsHelper {
    public static ItemStack stackWithTranslatedName(Item item, String key) {
        ItemStack stack = new ItemStack(item);
        return stackWithTranslatedName(stack, key);
    }
    public static ItemStack stackWithTranslatedName(ItemStack itemStack, String key) {
        ItemStack copy = itemStack.copy();
        copy.set(
            DataComponents.CUSTOM_NAME,
            Component.translatable(key)
        );
        return copy;
    }

    public static ItemStack stackWithTranslatedLore(Item item, String key) {
        ItemStack stack = new ItemStack(item);
        return stackWithTranslatedLore(stack, key);
    }
    public static ItemStack stackWithTranslatedLore(ItemStack itemStack, String key) {
        ItemStack copy = itemStack.copy();
        copy.set(
            DataComponents.LORE,
            ItemLore.EMPTY.withLineAdded(
                Component.translatable(key)
            )
        );
        return copy;
    }

    public static ItemStack getItemWithGlint(Item item) {
        ItemStack itemStack = new ItemStack(item);
        return getItemWithGlint(itemStack);
    }
    public static ItemStack getItemWithGlint(ItemStack item) {
        ItemStack copy = item.copy();
        copy.set(
            DataComponents.ENCHANTMENT_GLINT_OVERRIDE,
            true
        );
        return copy;
    }
}
