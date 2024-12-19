package me.wheelershigley.unlimited_anvil;

import me.wheelershigley.unlimited_anvil.item_categories.ItemCategories;
import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
        LOGGER.info("start");
        reloadConfigurations();
        initializeToolMaterials();
    }
}
