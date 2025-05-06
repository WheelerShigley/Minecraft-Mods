package me.wheelershigley.charged;

import me.wheelershigley.charged.command.CommandRegistrar;
import me.wheelershigley.charged.config.Configurations;
import me.wheelershigley.charged.helper.ConfigurationsHelper;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Charged implements ModInitializer {
    public static final String MOD_ID = "Charged";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Configurations configurations = ConfigurationsHelper.getConfiguations();
    public static boolean enablePlayerHeadDrops         =   (boolean)configurations.getConfiguration("enablePlayerHeadDrops"        ).getDefaultValue();
    public static boolean PlayerHeadsUseSkins           =   (boolean)configurations.getConfiguration("PlayerHeadsUseSkins"          ).getDefaultValue();
    public static long    MaximumDropsPerChargedCreeper =   (long)   configurations.getConfiguration("MaximumDropsPerChargedCreeper").getDefaultValue();

    @Override
    public void onInitialize() {
        CommandRegistrar.registerCommands();
    }

    public static void reload() {
        configurations.reload();

        enablePlayerHeadDrops         =   (boolean)configurations.getConfiguration("enablePlayerHeadDrops"        ).getValue();
        PlayerHeadsUseSkins           =   (boolean)configurations.getConfiguration("PlayerHeadsUseSkins"          ).getValue();
        MaximumDropsPerChargedCreeper =   (long)   configurations.getConfiguration("MaximumDropsPerChargedCreeper").getValue();
    }
}
