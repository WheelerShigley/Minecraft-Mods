package me.wheelershigley.trampleless;

import net.fabricmc.api.ModInitializer;

import static me.wheelershigley.trampleless.TramplelessGameRules.registerGameRules;

public class Trampleless implements ModInitializer {
    public static boolean farmlandTrampling = true;
    public static boolean featherFallingTrampling = false;

    @Override
    public void onInitialize() {
        registerGameRules();
    }
}
