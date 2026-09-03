package me.wheelershigley.www.solace_fishing.registrations;

import me.wheelershigley.www.solace_fishing.api.fishing.ClimatePreference;
import me.wheelershigley.www.solace_fishing.api.fishing.ClimatePreferencedItem;
import me.wheelershigley.www.solace_fishing.api.fishing.FishItem;
import me.wheelershigley.www.solace_fishing.api.statistics.Distribution;
import me.wheelershigley.www.solace_fishing.api.statistics.NormalDistribution;
import me.wheelershigley.www.solace_fishing.api.statistics.UniformDistribution;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class FishItems extends PolymerItemsRegister {
    public static final Item.Properties DEFAULT_RAW_FISH_ITEM_PROPERTIES = new Item.Properties().food(
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
        // Vanilla fishes, available everywhere; 60%:25%:13%:2%, like vanilla
        VANILLA_COD             = simpleRegister("vanilla_cod",             Items.COD,              0.60, null);
        VANILLA_SALMON          = simpleRegister("vanilla_salmon",          Items.SALMON,           0.25, null);
        VANILLA_PUFFERFISH      = simpleRegister("vanilla_pufferfish",      Items.PUFFERFISH,       0.13, null);
        VANILLA_TROPICAL_FISH   = simpleRegister("vanilla_tropical_fish",   Items.TROPICAL_FISH,    0.02, null);

        final ClimatePreference ANGELFISH_PREFERENCE = (new ClimatePreference.Builder())
            .withWeirdnessPreference( new NormalDistribution(0.3, 0.1) )
            .build()
        ;
        ANGELFISH       = simpleRegister("angelfish",       FishItems.ANGELFISH,        1.0, ANGELFISH_PREFERENCE);
        YELLOWFIN_TUNA  = simpleRegister("yellowfin_tuna",  FishItems.YELLOWFIN_TUNA,   1.0, null);
        ALBACORE_TUNA   = simpleRegister("albacore_tuna",   FishItems.ALBACORE_TUNA,    1.0, null);

        setSupplier(ANGELFISH);
        setSupplier(YELLOWFIN_TUNA);
        setSupplier(ALBACORE_TUNA);
    }

    private static FishItem simpleRegister(
        String name, Item item,
        double probability_density, ClimatePreference preference
    ) {
        return register(
            name,
            DEFAULT_RAW_FISH_ITEM_PROPERTIES,
            getFishProperties(
                new NormalDistribution(0.0, 1.0),
                item,
                probability_density,
                preference
            )
        );
    }

    private static void setSupplier(FishItem fishItem) {
        fishItem.preferences.setItemSupplier(
            () -> new ItemStack(fishItem)
        );
    }

    private static Function<Item.Properties, FishItem> getFishProperties(
        Distribution lengthDistribution,
        Item item, double probability_density,
        @Nullable ClimatePreference preferences
    ) {
        return properties -> new FishItem(
            properties,
            lengthDistribution,
            new UniformDistribution(0.5),
            new ClimatePreferencedItem(
                () -> new ItemStack(item),
                preferences == null ? ClimatePreference.DEFAULT_PREFERENCE : preferences,
                probability_density
            )
        );
    }

}
