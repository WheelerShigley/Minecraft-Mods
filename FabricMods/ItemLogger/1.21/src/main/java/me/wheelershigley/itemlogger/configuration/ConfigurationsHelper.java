package me.wheelershigley.itemlogger.configuration;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

import static me.wheelershigley.itemlogger.ItemLogger.MOD_ID;

public class ConfigurationsHelper {
    public static Configurations getConfigurations() {
        final String configurationsFileName = (MOD_ID + ".properties").toLowerCase();
        final File configurationsFile = FabricLoader.getInstance().getConfigDir().resolve(configurationsFileName).toFile();
        final Configurations configurations = new Configurations(configurationsFile, configurationsFileName);

        configurations.addConfiguration(
            new Configuration<>(
                "mode",
                "log",
                "Valid modes: \"off\", \"log\"."
            )
        );

        return configurations;
    }
}
