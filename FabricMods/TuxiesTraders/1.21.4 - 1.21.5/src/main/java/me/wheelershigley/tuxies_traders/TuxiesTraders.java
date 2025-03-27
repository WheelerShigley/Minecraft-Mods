package me.wheelershigley.tuxies_traders;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TuxiesTraders implements ModInitializer {
    public static final String MOD_ID = "tuxie_trade";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Wandering-Traders Overhauled!");
    }
}
