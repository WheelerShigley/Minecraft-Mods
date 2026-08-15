package me.wheelershigley.www.solace_fishing.registrations;

import me.wheelershigley.www.solace_fishing.implementations.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;

public class FishingItems extends PolymerItemsRegister {
    public static Item PDA;
    public static Item BAMBOO_ROD;

    //TODO: remove dummy accessories
    public static Item MAGENTA_HOOK;
    public static Item CYAN_HOOK;
    public static Item GREEN_LINE;
    public static Item YELLOW_LINE;
    public static Item RED_BOBBER;
    public static Item BLUE_BOBBER;
    public static Item RUBBER_DUCK_BOBBER;

    public static void initialize() {
        PDA = register(
            "pda",
            new Item.Properties().stacksTo(1),
            PDA::new
        );

        BAMBOO_ROD = register(
            "bamboo_rod",
            CustomFishingRod.DEFAULT_PROPERTIES,
            CustomFishingRod::new
        );

        MAGENTA_HOOK = register(
            "magenta_hook",
            Hook.DEFAULT_PROPERTIES,
            Hook::new
        );
        CYAN_HOOK = register(
            "cyan_hook",
            Hook.DEFAULT_PROPERTIES,
            Hook::new
        );

        GREEN_LINE = register(
            "green_line",
            Line.DEFAULT_PROPERTIES,
            Line::new
        );
        YELLOW_LINE = register(
            "yellow_line",
            Line.DEFAULT_PROPERTIES,
            Line::new
        );

        RED_BOBBER = register(
            "red_bobber",
            Bobber.DEFAULT_PROPERTIES,
            Bobber::new
        );
        BLUE_BOBBER = register(
            "blue_bobber",
            Bobber.DEFAULT_PROPERTIES,
            Bobber::new
        );


        final ItemLore rubberDuckLure = new ItemLore(
            List.of(
                Component
                    .translatable("item.solace_fishing.rubber_duck_bobber.description")
                    .withColor(TextColor.GRAY)
            )
        );
        RUBBER_DUCK_BOBBER = register(
            "rubber_duck_bobber",
            Bobber.DEFAULT_PROPERTIES.component(DataComponents.LORE, rubberDuckLure),
            Bobber::new
        );
    }
}
