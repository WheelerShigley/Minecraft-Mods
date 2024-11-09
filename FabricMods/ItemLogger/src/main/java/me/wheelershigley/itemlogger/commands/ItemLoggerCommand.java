package me.wheelershigley.itemlogger.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.wheelershigley.itemlogger.ItemLogger;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ItemLoggerCommand {
    private static final String PREFIX = "{"+ItemLogger.MOD_ID+"} ";
    private static Text getMessage(String message) {
        return Text.literal(PREFIX + message);
    }
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            CommandManager.literal("itemlogger").executes(
                context -> {
                    context.getSource().getPlayer().sendMessage(
                        getMessage("Called \"/itemlogger\" with no arguments.")
                    );
                    return -1;
                }
            )
            .then(
                CommandManager.literal("mode").executes(
                    context -> {
                        context.getSource().getPlayer().sendMessage(
                            getMessage("Called \"/itemlogger mode\" with no arguments.")
                        );
                        return -1;
                    }
                )
                /* "/itemlogger mode off" */
                .then(
                    CommandManager.literal("off").executes(
                        context -> {
                            ItemLogger.mode = ItemLogger.Mode.OFF;
                            context.getSource().getPlayer().sendMessage(
                                 getMessage("Disabled item-logging.")
                            );
                            return 0;
                        }
                    )
                )
                /* "/itemlogger mode log" */
                .then(
                    CommandManager.literal("log").executes(
                        context -> {
                            ItemLogger.mode = ItemLogger.Mode.LOG;
                            context.getSource().getPlayer().sendMessage(
                                getMessage("Item-logging to \"latest.log\".")
                            );
                            return 0;
                        }
                    )
                )
                /* "/itemlogger mode database" */ //FUTURE FEATURE
                /*.then(
                    CommandManager.literal("database").executes(
                        context -> {
                            ItemLogger.mode = ItemLogger.Mode.DATABASE;
                            context.getSource().getPlayer().sendMessage(
                                getMessage("Item-logging to database.")
                            );
                            return 0;
                        }
                    )
                )*/
            )
        );
    }
}
