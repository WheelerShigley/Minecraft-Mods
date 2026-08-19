package me.wheelershigley.www.solace_fishing.registrations;

import me.wheelershigley.www.solace_fishing.Constants;
import me.wheelershigley.www.solace_fishing.implementations.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;

import static me.wheelershigley.www.solace_fishing.Constants.*;

public class FishingItems extends PolymerItemsRegister {
    public static Item PDA;
    public static Item BAMBOO_ROD;

    //TODO: remove dummy accessories
    public static Item MAGENTA_HOOK;
    public static Item CYAN_HOOK;
    public static Item GREEN_LINE;
    public static Item YELLOW_LINE;
    public static Item BASIC_BOBBER;
    public static Item BELL_BOBBER;
    public static Item LUCKY_BOBBER;
    public static Item RUBBER_DUCK_BOBBER;

    private final static CustomData rubberDuckCustomData; static {
        CompoundTag customDataTag = new CompoundTag();
        customDataTag.putDouble(TRASH_MULTIPLIER_TAG, 2.0);
        customDataTag.putDouble(TREASURE_MULTIPLIER_TAG, 0.9);
        customDataTag.putDouble(CATCH_MULTIPLIER_TAG, 0.9);
        rubberDuckCustomData = CustomData.of(customDataTag);
    }

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

        BASIC_BOBBER = register(
            "basic_bobber",
            Bobber.DEFAULT_PROPERTIES,
            Bobber::new
        );
        BELL_BOBBER = register(
            "bell_bobber",
            Bobber.DEFAULT_PROPERTIES,
            Bobber::new
        );
        LUCKY_BOBBER = register(
            "lucky_bobber",
            Bobber
                .DEFAULT_PROPERTIES
                .component(
                    DataComponents.CUSTOM_DATA,
                    CustomData.of(
                        getIntegerTag(LUCK_TAG, 1)
                    )
                )
            ,
            Bobber::new
        );
        RUBBER_DUCK_BOBBER = register(
            "rubber_duck_bobber",
            Bobber
                .DEFAULT_PROPERTIES
                .component(DataComponents.CUSTOM_DATA, rubberDuckCustomData)
            ,
            Bobber::new
        );
    }

    private static CompoundTag getIntegerTag(String key, int value) {
        CompoundTag luckTag = new CompoundTag();
        luckTag.putInt(key, value);
        return luckTag;
    }
    private static CompoundTag getDoubleTag(String key, double value) {
        CompoundTag luckTag = new CompoundTag();
        luckTag.putDouble(key, value);
        return luckTag;
    }
}
