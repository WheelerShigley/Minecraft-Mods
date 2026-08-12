package me.wheelershigley.www.solace_fishing.helpers;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.Nullable;

public class ItemsHelper {
    public static ItemStack stackWithTranslatedName(Item item, String key) {
        ItemStack stack = new ItemStack(item);
        return stackWithTranslatedName(stack, key);
    }
    public static ItemStack stackWithTranslatedName(ItemStack itemStack, String key) {
        ItemStack copy = itemStack.copy();
        copy.set(
            DataComponents.CUSTOM_NAME,
            Component.translatable(key).withStyle(
                (style) ->  { return style.withItalic(false); }
            )
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

    public static ItemStack conditionallyWithTranslatedLore(boolean condition, ItemStack item, String key) {
        if(!condition) {
            return item;
        }
        return stackWithTranslatedLore(item, key);
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

    public static ItemStack getMenuItem(
        Item textureItem,
        boolean enchantable,
        @Nullable String translationKey
    ) {
        ItemStack itemStack = new ItemStack(Items.PAPER);

        itemStack.set(
            DataComponents.ITEM_MODEL,
            BuiltInRegistries.ITEM.getKey(textureItem)
        );

        CompoundTag menuItemTag = new CompoundTag();
        menuItemTag.putBoolean("MenuItem", true);
        itemStack.set(
            DataComponents.CUSTOM_DATA,
            CustomData.of(menuItemTag)
        );

        if(enchantable) {
            itemStack = getItemWithGlint(itemStack);
        }

        if(translationKey == null) {
            if(textureItem instanceof BlockItem) {
                translationKey = textureItem.getDescriptionId();
            } else {
                Identifier identifier = BuiltInRegistries.ITEM.getKey(textureItem);
                translationKey = "item." + identifier.getNamespace() + "." + identifier.getPath();
            }
        }
        itemStack = stackWithTranslatedName(itemStack, translationKey);

        return itemStack;
    }
}
