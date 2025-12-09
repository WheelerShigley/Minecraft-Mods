package me.wheelershigley.www.diegetic;

import net.fabricmc.api.ModInitializer;

import static me.wheelershigley.www.diegetic.gamerules.DiegeticGameRules.registerGameRules;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class Diegetic implements ModInitializer {
    public static final String MOD_ID = "diegetic";
//    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        registerGameRules();
    }
}
