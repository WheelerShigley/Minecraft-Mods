package me.wheelershigley.www.solace_fishing.api.fishing;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

public record FishingContext(
    Block medium,
    Item rod,
    float luck,
    RodAccessories accessories,
    ClimateData environment
) {
    @Override
    public @NonNull String toString() {
        return
            "FishingContext [" +
            "medium: " + medium +
            ", rod: " + rod +
            ", luck: " + luck +
            ", accessories: " + accessories +
            ", environment: " + environment +
            "]"
        ;
    }
}
