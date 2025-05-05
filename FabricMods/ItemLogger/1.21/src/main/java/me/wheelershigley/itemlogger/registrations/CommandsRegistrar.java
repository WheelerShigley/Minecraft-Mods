package me.wheelershigley.itemlogger.registrations;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.wheelershigley.itemlogger.ItemLogger;
import me.wheelershigley.itemlogger.client.ItemLoggerClient;
import me.wheelershigley.itemlogger.client.Mode;
import me.wheelershigley.itemlogger.client.Modes;
import me.wheelershigley.itemlogger.commands.ModesSuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class CommandsRegistrar {
    private static final String PREFIX = "<"+ ItemLogger.MOD_ID+"> ";
    private static Text getMessage(String message) {
        return Text.literal(PREFIX + message);
    }

    public static void register() {
        Command<FabricClientCommandSource> missingArguments = (context) -> {
            context.getSource().getPlayer().sendMessage(
                getMessage("Called \"/itemlogger\" with no arguments.")
            );
            return 1;
        };

        Command<FabricClientCommandSource> reloadCommand = (context) -> {
            ItemLoggerClient.configurations.reload();
            //TODO send message of reload
            return 0;
        };

        Command<FabricClientCommandSource> missingMode = (context) -> {
            context.getSource().getPlayer().sendMessage(
                    getMessage("Called \"/itemlogger mode\" with no arguments.")
            );
            return 2;
        };

        Command<FabricClientCommandSource> setMode = (context) -> {
            String attemptedMode = StringArgumentType.getString(context, "mode");
            Mode mode = Modes.toMode(attemptedMode);
            if(mode == null) {
                //TODO error message
                return 3;
            }

            //TODO change message
            ItemLoggerClient.mode = mode;
            return 0;
        };

        ClientCommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess) -> {
                dispatcher.register(
                    ClientCommandManager
                        .literal("itemlogger")
                        .executes(missingArguments)
                        .then(
                            ClientCommandManager
                                .literal("reload")
                                .executes(reloadCommand)
                        )
                        .then(
                            ClientCommandManager
                            .literal("mode")
                            .executes(missingMode)
                            .then(
                                ClientCommandManager.argument(
                                    "mode",
                                    StringArgumentType.string()
                                )
                                .suggests( new ModesSuggestionProvider() )
                                .executes(setMode)
                            )
                        )
                );
            }
        );
    }
}
