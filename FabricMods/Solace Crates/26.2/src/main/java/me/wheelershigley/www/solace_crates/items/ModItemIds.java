package me.wheelershigley.www.solace_crates.items;

import me.wheelershigley.www.solace_crates.SolaceCrates;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItemIds {
    //public static final ResourceKey<Item> TEST = create("test");

    public static ResourceKey<Item> create(String name) {
        return ResourceKey.create(
            Registries.ITEM,
            SolaceCrates.id(name)
        );
    }
}
