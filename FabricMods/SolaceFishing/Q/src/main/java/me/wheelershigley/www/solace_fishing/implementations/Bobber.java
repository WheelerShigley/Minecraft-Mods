package me.wheelershigley.www.solace_fishing.implementations;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Bobber extends Item implements PolymerItem  {
    public static final Item.Properties DEFAULT_PROPERTIES = new Item.Properties()
        .stacksTo(1)
        //.durability(64)
        //.enchantable(1)
    ;

    public Bobber(Properties properties) {
        super(properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.PAPER;
    }
}
