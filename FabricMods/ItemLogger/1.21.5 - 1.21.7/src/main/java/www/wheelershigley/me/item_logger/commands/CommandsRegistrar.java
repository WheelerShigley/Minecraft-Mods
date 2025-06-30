package www.wheelershigley.me.item_logger.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import www.wheelershigley.me.item_logger.ItemLogger;
import www.wheelershigley.me.item_logger.client.ItemLoggerClient;
import www.wheelershigley.me.item_logger.modes.Mode;
import www.wheelershigley.me.item_logger.modes.Modes;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class CommandsRegistrar {
    private static final String PREFIX; static {
        StringBuilder prefixBuilder = new StringBuilder();
        prefixBuilder
            .append('<')
            .append(
                Text.literal(
                    Text.translatable("item_logger.text.name").getString()
                ).getString()
            )
            .append("> ")
        ;
        PREFIX = prefixBuilder.toString();
    }
    private static final String commandName = ItemLogger.MOD_ID.replace("_","").toLowerCase();

    private static void sendPlayerTranslatableMessage(ClientPlayerEntity player, String translationText, Object... arguments) {
        player.sendMessage(
            Text.literal(
                PREFIX + Text.translatable(
                    translationText,
                    arguments
                ).getString()
            ),
            false
        );
    }

    public static void register() {
        Command<FabricClientCommandSource> missingArguments = (context) -> {
            sendPlayerTranslatableMessage(
                context.getSource().getPlayer(),
                "item_logger.command.text.itemlogger_no_arguments",
                commandName
            );
            return 1;
        };

        Command<FabricClientCommandSource> reloadCommand = (context) -> {
            ItemLoggerClient.reload();

            sendPlayerTranslatableMessage(
                context.getSource().getPlayer(),
                "item_logger.command.text.reloaded"
            );
            return 0;
        };

        Command<FabricClientCommandSource> missingMode = (context) -> {
            sendPlayerTranslatableMessage(
                context.getSource().getPlayer(),
                "item_logger.command.text.mode_no_arguments",
                commandName
            );
            return 2;
        };

        Command<FabricClientCommandSource> setMode = (context) -> {
            String attemptedMode = StringArgumentType.getString(context, "mode");
            Mode mode = Modes.toMode(attemptedMode);
            if(mode == null) {
                sendPlayerTranslatableMessage(
                    context.getSource().getPlayer(),
                    "item_logger.command.text.could_not_set_mode",
                    attemptedMode
                );
                return 3;
            }

            sendPlayerTranslatableMessage(
                context.getSource().getPlayer(),
                "item_logger.command.text.set_mode",
                attemptedMode
            );
            ItemLoggerClient.mode = mode;
            return 0;
        };

        ClientCommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess) -> {
                dispatcher.register(
                    ClientCommandManager
                        .literal( ItemLogger.MOD_ID.replace("_","").toLowerCase() )
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
