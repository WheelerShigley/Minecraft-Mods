package me.wheelershigley.www.trampleless;

import net.fabricmc.api.ModInitializer;

import static me.wheelershigley.www.trampleless.TramplelessGameRules.registerGameRules;

public class Trampleless implements ModInitializer {
    @Override
    public void onInitialize() {
        registerGameRules();
    }
}
