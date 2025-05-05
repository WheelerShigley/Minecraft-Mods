package me.wheelershigley.itemlogger.client;

import me.wheelershigley.itemlogger.commands.ItemLoggerCommand;
import me.wheelershigley.itemlogger.configuration.Configurations;
import me.wheelershigley.itemlogger.helper.ConfigurationHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class ItemLoggerClient implements ClientModInitializer {
    public static Configurations configurations = ConfigurationHelper.getConfigurations();
    public static Mode mode;

    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(
            ItemLoggerCommand::register
        );
    }

    /* TODO
     * add new configurations
     * correct command log/off modes
     * lang file
     * update to 1.21.5
     */

    @Override
    public void onInitializeClient() {
        registerCommands();
    }
}
