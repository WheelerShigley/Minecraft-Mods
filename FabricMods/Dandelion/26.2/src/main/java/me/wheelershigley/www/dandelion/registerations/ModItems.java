package me.wheelershigley.www.dandelion.registerations;

import me.wheelershigley.www.dandelion.item.Menu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
    public static final Item
        MENU                             = register(ModItemIds.MENU, Menu::new, Menu.PROPERTIES),

        BLUE_BOOK_ICON                   = register( ModItemIds.BLUE_BOOK_ICON,                  Item::new, new Item.Properties() ),
        DISCORD_ICON                     = register( ModItemIds.DISCORD_ICON,                    Item::new, new Item.Properties() ),
        DANDELION_ICON                   = register( ModItemIds.DANDELION_ICON,                  Item::new, new Item.Properties() ),
        STORE_ICON                       = register( ModItemIds.STORE_ICON,                      Item::new, new Item.Properties() ),
        WIKI_ICON                        = register( ModItemIds.WIKI_ICON,                       Item::new, new Item.Properties() ),
        NETHERITE_TEMPLATE_ICON          = register( ModItemIds.NETHERITE_TEMPLATE_ICON,         Item::new, new Item.Properties() ),

        WHITE_PORTAL_ICON                = register( ModItemIds.WHITE_PORTAL_ICON,               Item::new, new Item.Properties() ),
        CYAN_PORTAL_ICON                 = register( ModItemIds.CYAN_PORTAL_ICON,                Item::new, new Item.Properties() ),
        RED_PORTAL_ICON                  = register( ModItemIds.RED_PORTAL_ICON,                 Item::new, new Item.Properties() ),
        PURPLE_PORTAL_ICON               = register( ModItemIds.PURPLE_PORTAL_ICON,              Item::new, new Item.Properties() ),
        PURPLE_RESOURCE_PORTAL_ICON      = register( ModItemIds.PURPLE_RESOURCE_PORTAL_ICON,     Item::new, new Item.Properties() ),
        LIGHT_BLUE_PORTAL_ICON           = register( ModItemIds.LIGHT_BLUE_PORTAL_ICON,          Item::new, new Item.Properties() ),
        END_PORTAL_ICON                  = register( ModItemIds.END_PORTAL_ICON,                 Item::new, new Item.Properties() ),
        LIGHT_BLUE_RESOURCE_PORTAL_ICON  = register( ModItemIds.LIGHT_BLUE_RESOURCE_PORTAL_ICON, Item::new, new Item.Properties() ),
        END_RESOURCE_PORTAL_ICON         = register( ModItemIds.END_RESOURCE_PORTAL_ICON,        Item::new, new Item.Properties() )
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