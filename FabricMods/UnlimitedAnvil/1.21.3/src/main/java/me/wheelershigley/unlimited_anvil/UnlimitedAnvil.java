package me.wheelershigley.unlimited_anvil;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.wheelershigley.unlimited_anvil.ToolMaterials.initializeToolMaterials;


public class UnlimitedAnvil implements ModInitializer {
    public static final String MOD_ID = "unlimited_anvil";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("start");
        initializeToolMaterials();
    }
}
