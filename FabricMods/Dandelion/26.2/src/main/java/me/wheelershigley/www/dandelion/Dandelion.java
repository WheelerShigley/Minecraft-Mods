package me.wheelershigley.www.dandelion;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.wheelershigley.www.dandelion.registerations.Creative;
import me.wheelershigley.www.dandelion.registerations.ModItems;
import net.fabricmc.api.ModInitializer;

public class Dandelion implements ModInitializer {
    public static final String MOD_ID = "dandelion";

    @Override
    public void onInitialize() {
        ModItems.initialize();
        Creative.registerItems();

        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.markAsRequired();
    }
}
