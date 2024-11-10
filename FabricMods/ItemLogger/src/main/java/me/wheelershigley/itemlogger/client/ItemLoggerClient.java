package me.wheelershigley.itemlogger.client;

import me.wheelershigley.itemlogger.ItemLogger;
import me.wheelershigley.itemlogger.commands.ItemLoggerCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import static com.mojang.text2speech.Narrator.LOGGER;

public class ItemLoggerClient implements ClientModInitializer {
    public enum Mode {
        OFF,
        LOG,
        DATABASE
    }
    public static ItemLoggerClient.Mode mode;

    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(
                ItemLoggerCommand::register
        );
    }

    @Override
    public void onInitializeClient() {
        mode = ItemLoggerClient.Mode.OFF;
        registerCommands();
        LOGGER.info("Item-Logger Initialized");
    }
}
