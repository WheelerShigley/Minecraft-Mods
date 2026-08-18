package me.wheelershigley.www.solace_fishing.implementations;

import net.minecraft.world.item.Item;

public class Bobber extends AccessoryItem  {
    public static final Item.Properties DEFAULT_PROPERTIES = new Item.Properties()
        .stacksTo(1)
        .durability(64)
    ;
    @Override
    public Properties getDefaultProperties() {
        return DEFAULT_PROPERTIES;
    }

    public Bobber(Properties properties) {
        super(properties);
    }
}
