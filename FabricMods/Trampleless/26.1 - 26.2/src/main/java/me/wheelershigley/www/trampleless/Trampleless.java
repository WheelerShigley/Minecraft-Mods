package me.wheelershigley.www.trampleless;

import net.fabricmc.api.ModInitializer;

import static me.wheelershigley.www.trampleless.TramplelessGameRules.registerGameRules;

public class Trampleless implements ModInitializer {
    public static final String MOD_ID = "trampleless";

    @Override
    public void onInitialize() {
        registerGameRules();
    }
}
