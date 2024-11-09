package me.wheelershigley.itemlogger;

import me.wheelershigley.itemlogger.commands.ItemLoggerCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemLogger implements ModInitializer {
    public static final String MOD_ID = "item_logger";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public enum Mode {
        OFF,
        LOG,
        DATABASE
    }
    public static Mode mode;
    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(
            ItemLoggerCommand::register
        );
    }

    @Override
    public void onInitialize() {
        mode = ItemLogger.Mode.OFF;
        registerCommands();
        LOGGER.info("Item-Logger Initialized");
    }
}

