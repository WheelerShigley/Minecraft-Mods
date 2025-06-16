package www.wheelershigley.me.item_logger.client;

import www.wheelershigley.me.item_logger.configuration.Configurations;
import www.wheelershigley.me.item_logger.configuration.ConfigurationsHelper;
import www.wheelershigley.me.item_logger.commands.CommandsRegistrar;
import www.wheelershigley.me.item_logger.modes.Mode;
import www.wheelershigley.me.item_logger.modes.Modes;
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
