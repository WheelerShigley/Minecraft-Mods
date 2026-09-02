package me.wheelershigley.www.solace_fishing.implementations;

import net.minecraft.world.item.Item;

public class Line extends AccessoryItem  {
    public static final Properties DEFAULT_PROPERTIES = new Properties()
        .stacksTo(1)
        .durability(64)
    ;
    @Override
    public Properties getDefaultProperties() {
        return DEFAULT_PROPERTIES;
    }

    public Line(Properties properties) {
        super(properties);
    }
}
