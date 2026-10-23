package me.wheelershigley.www.dandelion.registerations;

import me.wheelershigley.www.dandelion.item.Icon;
import me.wheelershigley.www.dandelion.item.Menu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
    public static final Item
        MENU                             = register(ModItemIds.MENU, Menu::new, Menu.PROPERTIES),

        BLUE_BOOK_ICON                   = register(ModItemIds.BLUE_BOOK_ICON,                  Icon::new, Icon.PROPERTIES),
        DISCORD_ICON                     = register(ModItemIds.DISCORD_ICON,                    Icon::new, Icon.PROPERTIES),
        DANDELION_ICON                   = register(ModItemIds.DANDELION_ICON,                  Icon::new, Icon.PROPERTIES),
        STORE_ICON                       = register(ModItemIds.STORE_ICON,                      Icon::new, Icon.PROPERTIES),
        WIKI_ICON                        = register(ModItemIds.WIKI_ICON,                       Icon::new, Icon.PROPERTIES),
        NETHERITE_TEMPLATE_ICON          = register(ModItemIds.NETHERITE_TEMPLATE_ICON,         Icon::new, Icon.PROPERTIES),

        WHITE_PORTAL_ICON                = register(ModItemIds.WHITE_PORTAL_ICON,               Icon::new, Icon.PROPERTIES),
        CYAN_PORTAL_ICON                 = register(ModItemIds.CYAN_PORTAL_ICON,                Icon::new, Icon.PROPERTIES),
        RED_PORTAL_ICON                  = register(ModItemIds.RED_PORTAL_ICON,                 Icon::new, Icon.PROPERTIES),
        PURPLE_PORTAL_ICON               = register(ModItemIds.PURPLE_PORTAL_ICON,              Icon::new, Icon.PROPERTIES),
        PURPLE_RESOURCE_PORTAL_ICON      = register(ModItemIds.PURPLE_RESOURCE_PORTAL_ICON,     Icon::new, Icon.PROPERTIES),
        LIGHT_BLUE_PORTAL_ICON           = register(ModItemIds.LIGHT_BLUE_PORTAL_ICON,          Icon::new, Icon.PROPERTIES),
        END_PORTAL_ICON                  = register(ModItemIds.END_PORTAL_ICON,                 Icon::new, Icon.PROPERTIES),
        LIGHT_BLUE_RESOURCE_PORTAL_ICON  = register(ModItemIds.LIGHT_BLUE_RESOURCE_PORTAL_ICON, Icon::new, Icon.PROPERTIES),
        END_RESOURCE_PORTAL_ICON         = register(ModItemIds.END_RESOURCE_PORTAL_ICON,        Icon::new, Icon.PROPERTIES)
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