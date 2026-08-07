package me.wheelershigley.www.solace_fishing.registrations;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import me.wheelershigley.www.solace_fishing.SolaceFishing;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class SolaceItems {
    private static final FoodProperties rawFishNutrition = new FoodProperties(2, 1, false);

    public static Item ANGELFISH;

    public static Item register(String name, Item.Properties properties) {
        return register(name, properties, SimplePolymerItem::new);
    }

    public static <T extends Item> T register(String name, Item.Properties settings, Function<Item.Properties, T> function) {
        Identifier identifier = Identifier.fromNamespaceAndPath(SolaceFishing.MOD_ID, name);

        T item = function.apply(
            settings.setId( ResourceKey.create(Registries.ITEM, identifier) )
        );
        Registry.register(BuiltInRegistries.ITEM, identifier, item);
        return item;
    }

    public static void initialize() {
        ANGELFISH = register( "angelfish",  new Item.Properties().food(rawFishNutrition) );
    }

}
