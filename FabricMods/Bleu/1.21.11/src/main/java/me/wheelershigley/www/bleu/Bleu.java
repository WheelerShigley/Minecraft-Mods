package me.wheelershigley.www.bleu;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.rule.GameRule;

import static me.wheelershigley.www.bleu.GameRule.registerGameRule;

public class Bleu implements ModInitializer {
    public static final String MOD_ID = "bleu";

    public static GameRule<Integer> BLUE_VARIANT_RARITY;

    @Override
    public void onInitialize() {
        registerGameRule();
    }
}
