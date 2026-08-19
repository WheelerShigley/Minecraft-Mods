package me.wheelershigley.www.solace_fishing.api.fishing;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ClimatePreferencedItem {
    private ItemStack datum;
    private final ClimatePreference preference;
    private double area_multiplier = 1.0;

    public ClimatePreferencedItem(
        ItemStack datum,
        ClimatePreference preference,
        double area_multiplier
    ) {
        this.datum = datum;
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

    public void setItem(ItemStack stack) {
        this.datum = stack;
    }
    public ItemStack getItem() {
        return datum.copy();
    }

    @Override
    public ClimatePreferencedItem clone() {
        return new ClimatePreferencedItem(
            this.datum.copy(),
            this.preference,
            this.area_multiplier
        );
    }
}
