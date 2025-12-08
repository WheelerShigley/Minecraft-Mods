package me.wheelershigley.www.bleu;

import net.fabricmc.api.ModInitializer;

import static me.wheelershigley.www.bleu.RarityGameRule.registerGameRule;

public class Bleu implements ModInitializer {
    public static int BLUE_VARIANT_RARITY = 1200;

    @Override
    public void onInitialize() {
        registerGameRule();
    }
}
