package me.wheelershigley.itemlogger.client;

import me.wheelershigley.itemlogger.ItemLogger;
import me.wheelershigley.itemlogger.commands.ItemLoggerCommand;
import me.wheelershigley.itemlogger.config.Config;
import me.wheelershigley.itemlogger.config.ConfigHelpers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.Item;

import java.util.HashMap;

import static com.mojang.text2speech.Narrator.LOGGER;

public class ItemLoggerClient implements ClientModInitializer {
    public static Config configs;

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
        /*Load/Generate Local Configurations*/{
            HashMap<String, String> DefaultConfigs; {
                DefaultConfigs = new HashMap<>();

                DefaultConfigs.put("DefaultMode", "off"+" #valid modes: \"off\", \"log\"");//, \"database\"");
                //DefaultConfigs.put("DatabaseIP", "127.0.0.1:3306"+" #ip to schema'd database");
                //DefaultConfigs.put("DatabaseType", "MySQL"+" #supported languages: \"MySQL\"\r\n# See <> for Schema reference.");
            }
            configs = new Config(DefaultConfigs);

            mode = ConfigHelpers.getDefaultMode();
        }

        registerCommands();
        LOGGER.info("Item-Logger Initialized");
    }
}
