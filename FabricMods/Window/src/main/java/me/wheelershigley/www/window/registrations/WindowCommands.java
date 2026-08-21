package me.wheelershigley.www.window.registrations;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.wheelershigley.www.window.api.LevelArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;

public class WindowCommands implements ModInitializer {
    @Override
    public void onInitialize() {
        registerCommands();
    }

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                // Window Command, with Aliases
                dispatcher.getRoot().addChild(WINDOW_COMMAND);
                dispatcher.register(
                    Commands.literal("win")
                        .redirect(WINDOW_COMMAND)
                        .executes( WINDOW_COMMAND.getCommand() )
                );
                dispatcher.register(
                    Commands.literal("mw")
                        .redirect(WINDOW_COMMAND)
                        .executes( WINDOW_COMMAND.getCommand() )
                );
            }
        );
    }
    private static final LiteralCommandNode<CommandSourceStack> WINDOW_COMMAND = Commands.literal("window")
        .requires(WindowCommands::getWindowsCommandPermission)
        .then( tpCommandlet("tp") )
        .then( tpCommandlet("goto") )
        .build()
    ;
    private static ArgumentBuilder<CommandSourceStack, ?> tpCommandlet(String name) {
        return Commands.literal(name)
            .requires(WindowCommands::getWindowsCommandPermission)
            .then(
                Commands.argument("player", EntityArgument.player() )
                    .then(
                        Commands.argument("level", new LevelArgumentType() )
                            .suggests(LevelArgumentType::listStaticSuggestions)
                            .executes(WindowCommands::executeTeleport)
                    )
            )
        ;
    }
    private static boolean getWindowsCommandPermission(CommandSourceStack source) {
        return source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR);
    }

    private static int executeTeleport(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");

        ResourceKey<Level> levelKey = context.getArgument("level", ResourceKey.class);
        ServerLevel serverLevel = context.getSource().getServer().getLevel(levelKey);
        if(serverLevel == null){
            return -1;
        }

        if( player.level().equals(serverLevel) ){
            return 1;
        }

        //TODO: FIND A SAFE LOCATION TO TELEPORT TO
        player.teleport(
            new TeleportTransition(
                serverLevel,
                player.position(), //TODO: interdimensional-scale transform
                player.getDeltaMovement(),
                player.getYRot(),
                player.getXRot(),
                TeleportTransition.PLAY_PORTAL_SOUND
            )
        );

        return 1;
    }
}
