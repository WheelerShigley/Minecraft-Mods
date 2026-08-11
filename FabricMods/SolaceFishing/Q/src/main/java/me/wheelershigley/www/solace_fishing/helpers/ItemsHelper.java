package me.wheelershigley.www.solace_fishing.helpers;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemsHelper {
    public static ItemStack stackWithTranslatedName(Item item, String key) {
        ItemStack stack = new ItemStack(item);
        stack.set(
            DataComponents.CUSTOM_NAME,
            Component.translatable(key)
        );
        return stack;
    }
}
