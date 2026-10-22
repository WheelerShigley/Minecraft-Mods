package me.wheelershigley.www.dandelion.registerations;

import me.wheelershigley.www.dandelion.Dandelion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItemIds {
    public static final ResourceKey<Item>
        MENU = create("menu")
    ;

    private static ResourceKey<Item> create(String name) {
        return ResourceKey.create(
            Registries.ITEM,
            getModIdentifier(name)
        );
    }
    private static Identifier getModIdentifier(String name) {
        return Identifier.fromNamespaceAndPath(Dandelion.MOD_ID, name);
    }
}
