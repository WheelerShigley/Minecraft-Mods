package me.wheelershigley.www.solace_crates.items;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class ItemsHelper {
    public static Identifier getIdentifier(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
    public static @Nullable Item getItem(Identifier identifier) {
        return BuiltInRegistries.ITEM.getOptional(identifier).orElse(null);
    }
}
