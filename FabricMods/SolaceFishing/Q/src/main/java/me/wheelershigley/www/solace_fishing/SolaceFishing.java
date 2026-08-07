package me.wheelershigley.www.solace_fishing;

import me.wheelershigley.www.solace_fishing.registrations.ItemsGroup;
import me.wheelershigley.www.solace_fishing.registrations.SolaceItems;
import net.fabricmc.api.ModInitializer;

public class SolaceFishing implements ModInitializer {
    public static final String MOD_ID = "solace_fishing";

    @Override
    public void onInitialize() {
        SolaceItems.initialize();
        ItemsGroup.initialize();
    }
}
