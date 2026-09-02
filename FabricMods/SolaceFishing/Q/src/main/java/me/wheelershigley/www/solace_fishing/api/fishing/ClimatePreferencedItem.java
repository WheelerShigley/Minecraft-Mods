package me.wheelershigley.www.solace_fishing.api.fishing;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ClimatePreferencedItem {
    private Supplier<ItemStack> datumSupplier;
    private final ClimatePreference preference;
    private double area_multiplier = 1.0;

    public ClimatePreferencedItem(
        Supplier<ItemStack> datumSupplier,
        ClimatePreference preference,
        double area_multiplier
    ) {
        this.datumSupplier = datumSupplier;
        this.preference = preference;
        setArea(area_multiplier);
    }

    public boolean isInBounds(@NotNull ClimateData data) {
        return preference.isInBounds(data);
    }
    public double getAverageWeightAt(ClimateData locationData) {
        return area_multiplier * preference.getAverageWeightAt(locationData);
    }
    public double getAveragePickiness() {
        //TODO: account for biome/dimension -pickiness
        return preference.getAveragePickiness();
    }

    public void setArea(double area) {
        // |area_multiplier|
        if(area < 0.0) {
            area = -area;
        }

        this.area_multiplier = area;
    }
    public double getArea() {
        return this.area_multiplier;
    }

    public void setItemSupplier(Supplier<ItemStack> stackSupplier) {
        this.datumSupplier = stackSupplier;
    }
    public ItemStack getItem() {
        return datumSupplier.get();
    }

    @Override
    public ClimatePreferencedItem clone() {
        return new ClimatePreferencedItem(
            () -> this.datumSupplier.get(),
            this.preference.clone(),
            this.area_multiplier
        );
    }
}
