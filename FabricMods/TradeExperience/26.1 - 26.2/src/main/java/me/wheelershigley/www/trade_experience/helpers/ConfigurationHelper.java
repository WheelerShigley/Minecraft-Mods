package me.wheelershigley.www.trade_experience.helpers;

import net.fabricmc.loader.api.FabricLoader;
import me.wheelershigley.www.trade_experience.config.Configuration;
import me.wheelershigley.www.trade_experience.config.Configurations;

import java.io.File;

import static me.wheelershigley.www.trade_experience.TradeExperience.MOD_ID;

public class ConfigurationHelper {
    public static Configurations createTradeExperienceConfigurations() {
        final String configurationsFileName = (MOD_ID + ".properties").toLowerCase();
        final File configurationsFile = FabricLoader.getInstance().getConfigDir().resolve(configurationsFileName).toFile();
        final Configurations configurations = new Configurations(configurationsFile, configurationsFileName);

        configurations.addConfiguration(
            new Configuration<>(
                "experience_name",
                "experience",
                "Monetary-like experience name."
            )
        );

        return configurations;
    }
}
