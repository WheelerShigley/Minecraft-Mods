package me.wheelershigley.tuxietrade.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

public class ItemConvertibleSpecific implements ItemConvertible {
    private final Item specificItem;

    public ItemConvertibleSpecific(Item item) {
        specificItem = item;
    }

    @Override
    public Item asItem() {
        return specificItem;
    }
}
