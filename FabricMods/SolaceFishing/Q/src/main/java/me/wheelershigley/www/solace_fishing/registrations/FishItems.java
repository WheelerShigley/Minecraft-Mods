package me.wheelershigley.www.solace_fishing.registrations;

import me.wheelershigley.www.solace_fishing.api.fishing.ClimatePreference;
import me.wheelershigley.www.solace_fishing.api.fishing.ClimatePreferencedItem;
import me.wheelershigley.www.solace_fishing.api.fishing.FishItem;
import me.wheelershigley.www.solace_fishing.api.statistics.NormalDistribution;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class FishItems extends PolymerItemsRegister {
    public static final Item.Properties DEFAULT_FISH_PROPERTIES = new Item.Properties().food(
        new FoodProperties(2, 0.4f, false)
    );

    public static FishItem
        VANILLA_COD,
        VANILLA_SALMON,
        VANILLA_PUFFERFISH,
        VANILLA_TROPICAL_FISH
    ;
    public static FishItem
        ALBACORE_TUNA,
        ANGELFISH,
        YELLOWFIN_TUNA
    ;

    public static void initialize() {
        VANILLA_COD             = simpleRegister("vanilla_cod",             Items.COD,              null);
        VANILLA_SALMON          = simpleRegister("vanilla_salmon",          Items.SALMON,           null);
        VANILLA_PUFFERFISH      = simpleRegister("vanilla_pufferfish",      Items.PUFFERFISH,       null);
        VANILLA_TROPICAL_FISH   = simpleRegister("vanilla_tropical_fish",   Items.TROPICAL_FISH,    null);

        final ClimatePreference ANGELFISH_PREFERENCE = (new ClimatePreference.Builder())
            .withWeirdnessPreference( new NormalDistribution(0.3, 0.1) )
            .build()
        ;
        ANGELFISH       = simpleRegister("angelfish",       FishItems.ANGELFISH,        ANGELFISH_PREFERENCE);
        YELLOWFIN_TUNA  = simpleRegister("yellowfin_tuna",  FishItems.YELLOWFIN_TUNA,   null);
        ALBACORE_TUNA   = simpleRegister("albacore_tuna",   FishItems.ALBACORE_TUNA,    null);

        setSupplier(ANGELFISH);
        setSupplier(YELLOWFIN_TUNA);
        setSupplier(ALBACORE_TUNA);
    }

    private static FishItem simpleRegister(String name, Item item, ClimatePreference preference) {
        return register(
            name,
            DEFAULT_FISH_PROPERTIES,
            getFishProperties(1.0, 1.0, item, preference)
        );
    }

    private static void setSupplier(FishItem fishItem) {
        fishItem.preferences.setItemSupplier(
            () -> new ItemStack(fishItem)
        );
    }

    private static Function<Item.Properties, FishItem> getFishProperties(
        double mean,
        double standard_deviation,
        Item item,
        @Nullable ClimatePreference preferences
    ) {
        return properties -> new FishItem(
            properties,
            new NormalDistribution(mean, standard_deviation),
            new ClimatePreferencedItem(
                () -> new ItemStack(item),
                preferences == null ? ClimatePreference.DEFAULT_PREFERENCE : preferences,
                1.0
            )
        );
    }

}
