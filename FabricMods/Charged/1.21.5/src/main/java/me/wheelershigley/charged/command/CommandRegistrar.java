package me.wheelershigley.charged.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.wheelershigley.charged.Charged;
import me.wheelershigley.charged.helper.MessagesHelper;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.Predicate;

import static me.wheelershigley.charged.Charged.MOD_ID;

public class CommandRegistrar {
    private static final Predicate<ServerCommandSource> isServerOrOperator = (source) -> {
        if( !source.isExecutedByPlayer() ) {
            return true;
        }
        ServerPlayerEntity sourcePlayer = source.getPlayer();
        if(sourcePlayer == null) {
            return false;
        }

        return sourcePlayer.server.getPlayerManager().isOperator(
            sourcePlayer.getGameProfile()
        );
    };

    public static void registerCommands() {
        // /charged reload
        Command<ServerCommandSource> reloadCommand = (context) -> {
            ServerPlayerEntity player = context.getSource().getPlayer();

            if(player != null && player.getPermissionLevel() == 0) {
                MessagesHelper.sendChatMessage(player, "charged.command.text.insufficient_permission");
            }

            String sublet = StringArgumentType.getString(context, "sublet");
            if( sublet.equalsIgnoreCase("reload") ) {
                Charged.reload();

                if (player != null) {
                    MessagesHelper.sendChatMessage(player, "charged.command.text.reloaded");
                }
                return 0;
            }
            return 1;
        };

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                dispatcher.register(
                    CommandManager
                        .literal( MOD_ID.toLowerCase() )
                        .then(
                            CommandManager.argument(
                                "sublet",
                                StringArgumentType.string()
                            )
                            .suggests(new ReloadSuggestionProvider() )
                            .executes(reloadCommand)
                            .requires(isServerOrOperator)
                        )
                );
            }
        );
    }
}
