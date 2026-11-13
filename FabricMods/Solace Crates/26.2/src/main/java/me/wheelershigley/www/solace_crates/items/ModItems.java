package me.wheelershigley.www.solace_crates.items;

import com.google.common.base.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItems {
    public static void initialize() {}

    /*
    public static final Item
        CRATE = register(
            ModItemIds.CRATE,
            CrateItem::new,
            new Item.Properties()
        )
    ;
    */

    public static Item register(
        ResourceKey<Item> itemKey,
        Function<Item.Properties, Item> itemFactory,
        Item.Properties settings
    ) {
        Item item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }
}