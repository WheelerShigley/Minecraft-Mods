package me.wheelershigley.www.solace_fishing;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.wheelershigley.www.solace_fishing.registrations.FishItems;
import me.wheelershigley.www.solace_fishing.registrations.FishingItems;
import me.wheelershigley.www.solace_fishing.registrations.ItemGroup;
import me.wheelershigley.www.solace_fishing.registrations.ListenedEvents;
import net.fabricmc.api.ModInitializer;

public class SolaceFishing implements ModInitializer {
    public static final String MOD_ID = "solace_fishing";

    /* KNOWN BUGS
     * Enchanted Treasure Items don't appear Enchanted in the Probabilities-Menu (PDA)
     * Caught Enchanted-Books are sometimes unenchanted
     */

    @Override
    public void onInitialize() {
        FishItems.initialize();
        FishingItems.initialize();

        ItemGroup.initialize();
        ListenedEvents.register();

        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.markAsRequired();
    }
}
