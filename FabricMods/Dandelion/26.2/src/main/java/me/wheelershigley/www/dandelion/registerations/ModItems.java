package me.wheelershigley.www.dandelion.registerations;

import me.wheelershigley.www.dandelion.item.Menu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
    public static final Item
        MENU = register(ModItemIds.MENU, Menu::new, Menu.PROPERTIES)
    ;


    public static void initialize() {}

    private static Item register(
        ResourceKey<Item> itemKey,
        Function<Item.Properties, Item> itemFactory,
        Item.Properties settings
    ) {
        Item item = itemFactory.apply( settings.setId(itemKey) );
        return Registry.register(
            BuiltInRegistries.ITEM,
            itemKey.identifier(),
            item
        );
    }
}