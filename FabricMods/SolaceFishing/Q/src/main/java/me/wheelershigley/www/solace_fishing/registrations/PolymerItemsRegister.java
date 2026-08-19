package me.wheelershigley.www.solace_fishing.registrations;

import me.wheelershigley.www.solace_fishing.SolaceFishing;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public abstract class PolymerItemsRegister {
    public static <T extends Item> T register(String name, Item.Properties settings, Function<Item.Properties, T> function) {
        Identifier identifier = Identifier.fromNamespaceAndPath(SolaceFishing.MOD_ID, name);

        T item = function.apply(
            settings.setId( ResourceKey.create(Registries.ITEM, identifier) )
        );
        Registry.register(BuiltInRegistries.ITEM, identifier, item);
        return item;
    }
}
