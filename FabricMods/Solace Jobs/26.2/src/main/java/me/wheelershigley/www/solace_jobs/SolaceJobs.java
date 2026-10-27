package me.wheelershigley.www.solace_jobs;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SolaceJobs implements ModInitializer {
    public static final String MOD_ID = "solace_jobs";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        LOGGER.info(MOD_ID + " " + VERSION + " has started!");
    }

    public static Identifier solaceJobsIdentifier(String value) {
        return Identifier.fromNamespaceAndPath(MOD_ID, value);
    }
}
