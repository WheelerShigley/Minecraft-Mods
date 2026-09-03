package me.wheelershigley.www.solace_fishing.registrations;

import me.wheelershigley.www.solace_fishing.implementations.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;

import static me.wheelershigley.www.solace_fishing.Constants.*;

public class FishingItems extends PolymerItemsRegister {
    public static Item PDA;
    public static Item BAMBOO_ROD;

    public static Item TINY_HOOK;
    public static Item STANDARD_HOOK;
    public static Item LARGE_HOOK;
    public static Item SURFACE_LINE;
    public static Item CENTER_LINE;
    public static Item FLOOR_LINE;
    public static Item DOUBLE_LINE;
    public static Item BASIC_BOBBER;
    public static Item BELL_BOBBER;
    public static Item LUCKY_BOBBER;
    public static Item RUBBER_DUCK_BOBBER;

    private static final CustomData
        RUBBER_DUCK_BOBBER_CUSTOM_DATA,
        SURFACE_LINE_CUSTOM_DATA,
        CENTER_LINE_CUSTOM_DATA,
        FLOOR_LINE_CUSTOM_DATA,
        DOUBLE_LINE_CUSTOM_DATA,
        TINY_HOOK_CUSTOM_DATA,
        STANDARD_HOOK_CUSTOM_DATA,
        LARGE_HOOK_CUSTOM_DATA
    ;
    static {
        CompoundTag rubberDuckTag = new CompoundTag();
        rubberDuckTag.putDouble(TRASH_MULTIPLIER_TAG, 2.0);
        rubberDuckTag.putDouble(TREASURE_MULTIPLIER_TAG, 0.9);
        rubberDuckTag.putDouble(CATCH_MULTIPLIER_TAG, 0.9);
        RUBBER_DUCK_BOBBER_CUSTOM_DATA = CustomData.of(rubberDuckTag);

        CompoundTag surfaceTag = new CompoundTag();
        surfaceTag.putDouble(DEPTH_MINIMUM_PERCENTAGE, 2.0/3.0);
        SURFACE_LINE_CUSTOM_DATA = CustomData.of(surfaceTag);

        CompoundTag centerTag = new CompoundTag();
        centerTag.putDouble(DEPTH_MINIMUM_PERCENTAGE, 1.0/3.0);
        centerTag.putDouble(DEPTH_MAXIMUM_PERCENTAGE, 2.0/3.0);
        CENTER_LINE_CUSTOM_DATA = CustomData.of(centerTag);

        CompoundTag floorTag = new CompoundTag();
        floorTag.putDouble(DEPTH_MAXIMUM_PERCENTAGE, 1.0/3.0);
        FLOOR_LINE_CUSTOM_DATA = CustomData.of(floorTag);

        CompoundTag doubleTag = new CompoundTag();
        doubleTag.putDouble(DEPTH_MAXIMUM_PERCENTAGE, 2.0);
        DOUBLE_LINE_CUSTOM_DATA = CustomData.of(doubleTag);

        CompoundTag tinyHookTag = new CompoundTag();
        tinyHookTag.putDouble(SIZE_PERCENTILE_MULTIPLIER, 0.95);
        TINY_HOOK_CUSTOM_DATA = CustomData.of(tinyHookTag);

        CompoundTag standardHookTag = new CompoundTag();
        standardHookTag.putDouble(SIZE_PERCENTILE_MULTIPLIER, 0.95);
        STANDARD_HOOK_CUSTOM_DATA = CustomData.of(standardHookTag);

        CompoundTag largeHookTag = new CompoundTag();
        largeHookTag.putDouble(SIZE_PERCENTILE_MULTIPLIER, 0.95);
        LARGE_HOOK_CUSTOM_DATA = CustomData.of(largeHookTag);
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

        TINY_HOOK = register(
            "tiny_hook",
            Hook.DEFAULT_PROPERTIES.component(DataComponents.CUSTOM_DATA, TINY_HOOK_CUSTOM_DATA),
            Hook::new
        );
        STANDARD_HOOK = register(
            "standard_hook",
            Hook.DEFAULT_PROPERTIES.component(DataComponents.CUSTOM_DATA, STANDARD_HOOK_CUSTOM_DATA),
            Hook::new
        );
        LARGE_HOOK = register(
            "large_hook",
            Hook.DEFAULT_PROPERTIES.component(DataComponents.CUSTOM_DATA, LARGE_HOOK_CUSTOM_DATA),
            Hook::new
        );

        SURFACE_LINE = register(
            "surface_line",
            Line.DEFAULT_PROPERTIES.component(DataComponents.CUSTOM_DATA, SURFACE_LINE_CUSTOM_DATA),
            Line::new
        );
        CENTER_LINE = register(
            "center_line",
            Line.DEFAULT_PROPERTIES.component(DataComponents.CUSTOM_DATA, CENTER_LINE_CUSTOM_DATA),
            Line::new
        );
        FLOOR_LINE = register(
            "floor_line",
            Line.DEFAULT_PROPERTIES.component(DataComponents.CUSTOM_DATA, FLOOR_LINE_CUSTOM_DATA),
            Line::new
        );
        DOUBLE_LINE = register(
            "double_line",
            Line.DEFAULT_PROPERTIES.component(DataComponents.CUSTOM_DATA, DOUBLE_LINE_CUSTOM_DATA),
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
            Bobber.DEFAULT_PROPERTIES.component(DataComponents.CUSTOM_DATA, RUBBER_DUCK_BOBBER_CUSTOM_DATA),
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
