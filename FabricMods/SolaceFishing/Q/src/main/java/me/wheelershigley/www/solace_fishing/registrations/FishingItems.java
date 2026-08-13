package me.wheelershigley.www.solace_fishing.registrations;

import me.wheelershigley.www.solace_fishing.implementations.*;
import net.minecraft.world.item.Item;

public class FishingItems extends PolymerItemsRegister {
    public static Item PDA;
    public static Item BAMBOO_ROD;

    public static Item A_HOOK;
    public static Item ANOTHER_HOOK;
    public static Item A_LINE;
    public static Item ANOTHER_LINE;
    public static Item RED_BOBBER;
    public static Item BLUE_BOBBER;

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

        A_HOOK = register(
            "a_hook",
            Hook.DEFAULT_PROPERTIES,
            Hook::new
        );
        ANOTHER_HOOK = register(
            "another_hook",
            Hook.DEFAULT_PROPERTIES,
            Hook::new
        );

        A_LINE = register(
            "a_line",
            Line.DEFAULT_PROPERTIES,
            Line::new
        );
        ANOTHER_LINE = register(
            "another_line",
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
    }
}
