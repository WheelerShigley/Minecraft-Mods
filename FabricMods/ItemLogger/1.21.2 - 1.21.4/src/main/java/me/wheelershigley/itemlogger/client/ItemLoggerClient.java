package me.wheelershigley.itemlogger.client;

import me.wheelershigley.itemlogger.configuration.Configurations;
import me.wheelershigley.itemlogger.configuration.ConfigurationsHelper;
import me.wheelershigley.itemlogger.commands.CommandsRegistrar;
import me.wheelershigley.itemlogger.modes.Mode;
import me.wheelershigley.itemlogger.modes.Modes;
import net.fabricmc.api.ClientModInitializer;

public class ItemLoggerClient implements ClientModInitializer {
    public static Configurations configurations = ConfigurationsHelper.getConfigurations();
    public static Mode mode = Modes.toMode(
        configurations.getConfiguration("mode").getDefaultConfiguration()
    );

    @Override
    public void onInitializeClient() {
        CommandsRegistrar.register();

        reload();
    }

    public static void reload() {
        ItemLoggerClient.configurations.reload();

        Mode attemptedMode = Modes.toMode(
            (String)configurations.getConfiguration("mode").getValue()
        );
        if(attemptedMode == null) {
            mode = Modes.toMode(
                configurations.getConfiguration("mode").getDefaultConfiguration()
            );
        } else {
            mode = attemptedMode;
        }
    }
}
