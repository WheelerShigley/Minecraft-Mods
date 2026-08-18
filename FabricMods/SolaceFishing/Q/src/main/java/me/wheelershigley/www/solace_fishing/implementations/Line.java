package me.wheelershigley.www.solace_fishing.implementations;

import net.minecraft.world.item.Item;

public class Line extends AccessoryItem  {
    private static final Item.Properties LINE_DEFAULT_PROPERTIES = AccessoryItem.DEFAULT_PROPERTIES
        .durability(64)
    ;

    public Line() {
        super();
    }
    public Line(Properties properties) {
        super(properties);
    }
}
