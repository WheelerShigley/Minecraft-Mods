package me.wheelershigley.www.solace_fishing.implementations;

import eu.pb4.polymer.core.api.item.PolymerItem;
import me.wheelershigley.www.solace_fishing.registrations.CustomComponents;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;

public class Line extends Item implements PolymerItem  {
    public static final Item.Properties DEFAULT_PROPERTIES = new Item.Properties()
        .stacksTo(1)
        //.durability(64)
        //.enchantable(1)
    ;

    public Line(Properties properties) {
        super(properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.PAPER;
    }
}
