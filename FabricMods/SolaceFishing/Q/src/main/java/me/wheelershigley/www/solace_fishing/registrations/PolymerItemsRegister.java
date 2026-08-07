package me.wheelershigley.www.solace_fishing.registrations;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import me.wheelershigley.www.solace_fishing.SolaceFishing;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public abstract class PolymerItemsRegister {
    protected static void initialize() {}

    public static Item register(String name, Item.Properties properties) {
        return register(name, properties, SimplePolymerItem::new);
    }

    public static <T extends Item> T register(String name, Function<Item.Properties, T> function) {
        return register(name, new Item.Properties(), function);
    }

    public static <T extends Item> T register(String name, Item.Properties settings, Function<Item.Properties, T> function) {
        Identifier identifier = Identifier.fromNamespaceAndPath(SolaceFishing.MOD_ID, name);

        T item = function.apply(
                settings.setId( ResourceKey.create(Registries.ITEM, identifier) )
        );
        Registry.register(BuiltInRegistries.ITEM, identifier, item);
        return item;
    }
}
