package me.wheelershigley.unlimited_anvil;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static me.wheelershigley.unlimited_anvil.ToolMaterials.initializeToolMaterials;


public class UnlimitedAnvil implements ModInitializer {
    public static final String MOD_ID = "unlimited_anvil";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static HashMap<Identifier, Integer> MaximumEnchantLevels;
    public static HashMap<Identifier, Identifier[]> Conflicts;

    public static void reloadConfigurations() {
        MaximumEnchantLevels = HelperFunctions.getMaximumEffectiveEnchantLevels();
        Conflicts = HelperFunctions.getConflicts();
    }

    @Override
    public void onInitialize() {
        reloadConfigurations();
        initializeToolMaterials();
    }
}
