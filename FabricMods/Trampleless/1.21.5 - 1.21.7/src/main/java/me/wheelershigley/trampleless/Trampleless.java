package me.wheelershigley.trampleless;

import net.fabricmc.api.ModInitializer;

import static me.wheelershigley.trampleless.TramplelessGameRules.registerGameRules;

public class Trampleless implements ModInitializer {
    @Override
    public void onInitialize() {
        registerGameRules();
    }
}
