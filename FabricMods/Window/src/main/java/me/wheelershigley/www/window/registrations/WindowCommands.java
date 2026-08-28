package me.wheelershigley.www.window.registrations;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.wheelershigley.www.window.WindowConfig;
import me.wheelershigley.www.window.api.PortalDefinition;
import me.wheelershigley.www.window.portal.Portal;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.portal.TeleportTransition;

public class WindowCommands {
    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, context, selection) -> {
                final LiteralCommandNode<CommandSourceStack> WINDOW_COMMAND = getWindowCommand(context);

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
    private static LiteralCommandNode<CommandSourceStack> getWindowCommand(CommandBuildContext context) {
        return Commands.literal("window")
            .requires(WindowCommands::getWindowsCommandPermission)
            .then( tpCommandlet("tp") )
            .then( tpCommandlet("goto") )
            .then( linkCommandlet("link", context) )
            .build()
        ;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> tpCommandlet(String name) {
        return Commands.literal(name)
            .requires(WindowCommands::getWindowsCommandPermission)
            .then(
                Commands.argument("player", EntityArgument.player() )
                    .then(
                        Commands.argument("level", DimensionArgument.dimension() )
                    )
            )
        ;
    }
    private static boolean getWindowsCommandPermission(CommandSourceStack source) {
        return source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR);
    }
        private static int teleport(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            ServerLevel serverLevel = DimensionArgument.getDimension(context, "level");

        TeleportTransition transition = Portal.getTransition(player, serverLevel);
        if(transition == null) {
            return -1;
        }
        player.teleport(transition);
        return 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> linkCommandlet(String name, CommandBuildContext context) {
        return Commands.literal(name)
            .requires(WindowCommands::getWindowsCommandPermission)
            .then(
                Commands.literal("list")
                    .executes(WindowCommands::linksList)
            )
            .then(
                Commands.argument("material", BlockStateArgument.block(context) )
                    .then(
                        Commands.argument("igniter", BlockStateArgument.block(context) )
                            .then(
                                Commands.argument("from_level", DimensionArgument.dimension() )
                                    .then(
                                        Commands.argument("to_level", DimensionArgument.dimension() )
                                            .executes(WindowCommands::link)
                                    )
                            )
                    )
            )
        ;
    }
    private static int link(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockInput inputMaterial = BlockStateArgument.getBlock(context, "material");
        Block material = inputMaterial.getState().getBlock();
        BlockInput inputIgniter = BlockStateArgument.getBlock(context, "igniter");
        Block igniter = inputIgniter.getState().getBlock();
        ServerLevel fromLevel = DimensionArgument.getDimension(context, "from_level");
        ServerLevel   tolevel = DimensionArgument.getDimension(context, "to_level");

        WindowConfig.INSTANCE.definitions.add(
            new PortalDefinition(material, igniter, fromLevel.dimension(), tolevel.dimension() )
        );
        WindowPersistentConfigurations.save();
        return 0;
    }
    private static int linksList(CommandContext<CommandSourceStack> context) {
        ServerPlayer requestor = context.getSource().getPlayer();
        if(requestor == null) {
            return -1;
        }

        requestor.sendSystemMessage(
            Component.literal(
                WindowConfig.INSTANCE.toString()
            )
        );
        return 0;
    }
}
