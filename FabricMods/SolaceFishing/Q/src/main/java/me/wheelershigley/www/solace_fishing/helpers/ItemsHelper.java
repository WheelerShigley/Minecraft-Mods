package me.wheelershigley.www.solace_fishing.helpers;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemsHelper {
    public static ItemStack stackWithTranslatedName(Item item, @Nullable String key) {
        ItemStack stack = new ItemStack(item);
        return stackWithTranslatedName(stack, key);
    }
    public static ItemStack stackWithTranslatedName(ItemStack itemStack, @Nullable String key) {
        ItemStack copy = itemStack.copy();
        if(key != null) {
            copy.set(
                DataComponents.CUSTOM_NAME,
                Component.translatable(key).withStyle(
                    Style.EMPTY.withItalic(false)
                )
            );
        }
        return copy;
    }

    public static ItemStack stackWithTranslatedLore(Item item, @Nullable String key) {
        ItemStack stack = new ItemStack(item);
        return stackWithTranslatedLore(stack, key);
    }
    public static ItemStack stackWithTranslatedLore(ItemStack itemStack, @Nullable String key) {
        ItemStack copy = itemStack.copy();
        if(key != null) {
            copy.set(
                DataComponents.LORE,
                ItemLore.EMPTY.withLineAdded(
                    Component.translatable(key)
                )
            );
        }
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
        return getMenuItem(textureItem, null, enchantable, translationKey);
    }
    public static ItemStack getMenuItem(
        Item textureItem,
        @Nullable DataComponentMap components,  
        boolean enchantable,
        @Nullable String translationKey
    ) {
        Identifier textureIdentifier = BuiltInRegistries.ITEM.getKey(textureItem);
        return getMenuItem(
            translationKey != null ? translationKey : textureItem.getDescriptionId(),
            textureIdentifier,
            components,
            enchantable
        );
    }
    public static ItemStack getMenuItem(
        @Nullable String translationKey,
        Identifier textureIdentifier,
        @Nullable DataComponentMap components,
        boolean enchantable
    ) {
        ItemStack itemStack = new ItemStack(Items.PAPER);

        itemStack.set(DataComponents.ITEM_MODEL, textureIdentifier);
        if(components != null) {
            for(TypedDataComponent<?> component : components) {
                if(component.type() == DataComponents.ITEM_MODEL) {
                    continue;
                }

                setComponent(itemStack, component);
            }
        }

        if(translationKey != null) {
            itemStack.set(
                DataComponents.CUSTOM_NAME,
                Component.translatable(translationKey).withStyle( Style.EMPTY.withItalic(false) )
            );
        }

        CompoundTag menuItemTag = new CompoundTag();
        menuItemTag.putBoolean("MenuItem", true);
        itemStack.set(
            DataComponents.CUSTOM_DATA,
            CustomData.of(menuItemTag)
        );

        if(enchantable) {
            itemStack = getItemWithGlint(itemStack);
        }

        return stackWithTranslatedName(itemStack, translationKey);
    }

    private static <T> void setComponent(
        ItemStack itemStack,
        TypedDataComponent<T> component
    ) {
        itemStack.set(component.type(), component.value());
    }

    public static void appendLore(ItemStack item, Component component) {
        ItemLore lore = item.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);

        List<Component> lines = new ArrayList<>( lore.lines() );
        lines.add(component);
        item.set( DataComponents.LORE, new ItemLore(lines) );
    }
    public static void appendLore(ItemStack item, List<Component> components) {
        ItemLore lore = item.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);

        List<Component> lines = new ArrayList<>( lore.lines() );
        lines.addAll(components);
        item.set( DataComponents.LORE, new ItemLore(lines) );
    }
}
