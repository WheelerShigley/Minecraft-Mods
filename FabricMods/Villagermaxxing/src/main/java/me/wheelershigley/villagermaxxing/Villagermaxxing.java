package me.wheelershigley.villagermaxxing;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Villagermaxxing implements ModInitializer {
    public static final String MOD_ID = "villagermaxxing";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("test");
    }
}
