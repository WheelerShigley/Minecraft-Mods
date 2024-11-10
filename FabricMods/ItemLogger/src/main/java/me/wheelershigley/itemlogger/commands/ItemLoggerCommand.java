package me.wheelershigley.itemlogger.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.wheelershigley.itemlogger.ItemLogger;
import me.wheelershigley.itemlogger.client.ItemLoggerClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public class ItemLoggerCommand {
    private static final String PREFIX = "{"+ItemLogger.MOD_ID+"} ";
    private static Text getMessage(String message) {
        return Text.literal(PREFIX + message);
    }
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        dispatcher.register(
            ClientCommandManager.literal("itemlogger").executes(
                context -> {
                    context.getSource().getPlayer().sendMessage(
                        getMessage("Called \"/itemlogger\" with no arguments.")
                    );
                    return -1;
                }
            )
            .then(
                ClientCommandManager.literal("mode").executes(
                    context -> {
                        context.getSource().getPlayer().sendMessage(
                            getMessage("Called \"/itemlogger mode\" with no arguments.")
                        );
                        return -1;
                    }
                )
                /* "/itemlogger mode off" */
                .then(
                    ClientCommandManager.literal("off").executes(
                        context -> {
                            ItemLoggerClient.mode = ItemLoggerClient.Mode.OFF;
                            context.getSource().getPlayer().sendMessage(
                                 getMessage("Disabled item-logging.")
                            );
                            return 0;
                        }
                    )
                )
                /* "/itemlogger mode log" */
                .then(
                    ClientCommandManager.literal("log").executes(
                        context -> {
                            ItemLoggerClient.mode = ItemLoggerClient.Mode.LOG;
                            context.getSource().getPlayer().sendMessage(
                                getMessage("Item-logging to \"latest.log\".")
                            );
                            return 0;
                        }
                    )
                )
                /* "/itemlogger mode database" */ //FUTURE FEATURE
                /*.then(
                    ClientCommandManager.literal("database").executes(
                        context -> {
                            ItemLoggerClient.mode = ItemLoggerClient.Mode.DATABASE;
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
