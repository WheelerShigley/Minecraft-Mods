package me.wheelershigley.charged;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.wheelershigley.charged.gamerules.GameRuleRegistrar.registerGameRuleNetworking;
import static me.wheelershigley.charged.gamerules.GameRuleRegistrar.registerGameRules;

public class Charged implements ModInitializer {
    public static final String MOD_ID = "charged";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        registerGameRules();
        registerGameRuleNetworking();
    }
}
