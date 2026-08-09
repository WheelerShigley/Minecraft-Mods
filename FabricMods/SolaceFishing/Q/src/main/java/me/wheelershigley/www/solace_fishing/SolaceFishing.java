package me.wheelershigley.www.solace_fishing;

import me.wheelershigley.www.solace_fishing.registrations.FishItems;
import me.wheelershigley.www.solace_fishing.registrations.FishingItems;
import me.wheelershigley.www.solace_fishing.registrations.ItemGroup;
import net.fabricmc.api.ModInitializer;

public class SolaceFishing implements ModInitializer {
    public static final String MOD_ID = "solace_fishing";
    //TODO: fix that when rod-items are moved in the inventory, "cast" is not unset

    @Override
    public void onInitialize() {
        FishItems.initialize();
        FishingItems.initialize();

        ItemGroup.initialize();
    }
}
