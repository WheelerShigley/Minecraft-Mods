package me.wheelershigley.diegetic;

import me.wheelershigley.diegetic.gamerules.DiegeticGameRules;
import net.fabricmc.api.ModInitializer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import static me.wheelershigley.diegetic.gamerules.DiegeticGameRules.registerGameRules;

public class Diegetic implements ModInitializer {
//    public static final String MOD_ID = "diegetic";
//    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean diegeticClockDisplaysTime = true;
    public static boolean diegeticClockUsesServerTime = false;
    public static boolean diegeticCompassCoordinates = true;
    public static boolean diegeticLodestoneCompassRelativeCoordinates = true;
    public static boolean diegeticRecoveryCompassRelativeCoordinates = true;
    public static boolean diegeticSlimeChunkChecking = true;


    @Override
    public void onInitialize() {
        registerGameRules();
    }
}
