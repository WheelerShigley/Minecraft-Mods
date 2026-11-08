package me.wheelershigley.www.dandelion.registerations;

import me.wheelershigley.www.dandelion.Dandelion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItemIds {
    public static final ResourceKey<Item>
        MENU                             = create("menu"),

        BLUE_BOOK_ICON                   = create("blue_book_icon"),
        DISCORD_ICON                     = create("discord_icon"),
        DANDELION_ICON                   = create("dandelion_icon"),
        STORE_ICON                       = create("store_icon"),
        WIKI_ICON                        = create("wiki_icon"),
        NETHERITE_TEMPLATE_ICON          = create("netherite_template_icon"),

        RED_SWORD_ICON                   = create("red_sword_icon"),
        GREEN_SWORD_ICON                 = create("green_sword_icon"),
        RED_PICKAXE_ICON                 = create("red_pickaxe_icon"),
        GREEN_PICKAXE_ICON               = create("green_pickaxe_icon"),
        RED_SHOVEL_ICON                  = create("red_shovel_icon"),
        GREEN_SHOVEL_ICON                = create("green_shovel_icon"),
        RED_AXE_ICON                     = create("red_axe_icon"),
        GREEN_AXE_ICON                   = create("green_axe_icon"),
        RED_HOE_ICON                     = create("red_hoe_icon"),
        GREEN_HOE_ICON                   = create("green_hoe_icon"),
        RED_FISHING_ROD_ICON             = create("red_fishing_rod_icon"),
        GREEN_FISHING_ROD_ICON           = create("green_fishing_rod_icon"),
        RED_BRUSH_ICON                   = create("red_brush_icon"),
        GREEN_BRUSH_ICON                 = create("green_brush_icon"),

        WHITE_PORTAL_ICON                = create("white_portal_icon"),
        CYAN_PORTAL_ICON                 = create("cyan_portal_icon"),
        RED_PORTAL_ICON                  = create("red_portal_icon"),
        PURPLE_PORTAL_ICON               = create("purple_portal_icon"),
        PURPLE_RESOURCE_PORTAL_ICON      = create("purple_resource_portal_icon"),
        LIGHT_BLUE_PORTAL_ICON           = create("light_blue_portal_icon"),
        LIGHT_BLUE_RESOURCE_PORTAL_ICON  = create("light_blue_resource_portal_icon"),
        END_PORTAL_ICON                  = create("end_portal_icon"),
        END_RESOURCE_PORTAL_ICON         = create("end_resource_portal_icon")
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
