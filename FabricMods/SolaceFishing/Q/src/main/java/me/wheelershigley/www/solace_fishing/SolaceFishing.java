package me.wheelershigley.www.solace_fishing;

import me.wheelershigley.www.solace_fishing.registrations.FishItems;
import me.wheelershigley.www.solace_fishing.registrations.ItemGroup;
import me.wheelershigley.www.solace_fishing.registrations.RodItems;
import net.fabricmc.api.ModInitializer;

public class SolaceFishing implements ModInitializer {
    public static final String MOD_ID = "solace_fishing";

    //TODO: fix that when rod-items are moved in the inventory, "cast" is not unset

    @Override
    public void onInitialize() {
        FishItems.initialize();
        RodItems.initialize();

        ItemGroup.initialize();
    }
}
