package me.wheelershigley.www.window.registrations;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.wheelershigley.www.window.WindowConfig;
import me.wheelershigley.www.window.api.LinkType;
import me.wheelershigley.www.window.api.PortalDefinition;
import me.wheelershigley.www.window.portal.CustomPortal;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.portal.TeleportTransition;

import java.util.Locale;
import java.util.function.Supplier;

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

        TeleportTransition transition = CustomPortal.getTransition(player, serverLevel);
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
                Commands.literal("add").then( addCommandlet(context) )
            )
            .then(
                Commands.literal("remove").then( removeCommandlet(context) )
            )
            .then(
                Commands.literal("list").executes(WindowCommands::linksList)
            )
        ;
    }
    private static RequiredArgumentBuilder<CommandSourceStack, ?> addCommandlet(CommandBuildContext context) {
        return Commands.argument("material", BlockStateArgument.block(context) ).then(
            Commands.argument("igniter", BlockStateArgument.block(context) ).then(
                Commands.argument("from_level", DimensionArgument.dimension() ).then(
                    Commands.argument("to_level", DimensionArgument.dimension() ).then(
                        Commands
                            .argument("color", StringArgumentType.word() )
                            .suggests(
                                (_context, _builder) -> {
                                    for( DyeColor color : DyeColor.values() ) {
                                        _builder.suggest( color.getName() );
                                    }
                                    return _builder.buildFuture();
                                }
                            )
                            .then(
                                Commands
                                    .argument("type", StringArgumentType.word() )
                                    .suggests(
                                        (_context, _builder) -> {
                                            for( LinkType type : LinkType.values() ) {
                                                _builder.suggest(
                                                    type.name().toLowerCase(Locale.ROOT)
                                                );
                                            }
                                            return _builder.buildFuture();
                                        }
                                    )
                                    .executes(WindowCommands::link)
                            )
                    )
                )
            )
        );
    }
    private static RequiredArgumentBuilder<CommandSourceStack, ?> removeCommandlet(CommandBuildContext context) {
        return Commands
            .argument("material", BlockStateArgument.block(context) )
            .suggests(
                (_context, _builder) -> {
                    for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
                        _builder.suggest(
                            BuiltInRegistries.BLOCK.wrapAsHolder( definition.frameMaterial() ).getRegisteredName()
                        );
                    }
                    return _builder.buildFuture();
                }
            )
            .then(
                Commands
                    .argument("igniter", BlockStateArgument.block(context) )
                    .suggests(
                        (_context, _builder) -> {
                            Block material = getPriorArgument(_context, "material", BlockInput.class).getState().getBlock();
                            for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
                                if( !definition.frameMaterial().equals(material) ) {
                                    continue;
                                }
                                _builder.suggest(
                                    BuiltInRegistries.BLOCK.wrapAsHolder( definition.ignitionMaterial() ).getRegisteredName()
                                );
                            }
                            return _builder.buildFuture();
                        }
                    )
                    .then(
                        Commands
                            .argument("from_level", DimensionArgument.dimension() )
                            .suggests(
                                (_context, _builder) -> {
                                    Block frameMaterial = getPriorArgument(_context, "material", BlockInput.class).getState().getBlock();
                                    Block ignitionMaterial = getPriorArgument(_context, "igniter", BlockInput.class).getState().getBlock();
                                    for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
                                        if(
                                            !definition.frameMaterial().equals(frameMaterial)
                                            || !definition.ignitionMaterial().equals(ignitionMaterial)
                                        ) {
                                            continue;
                                        }
                                        _builder.suggest(
                                            definition.fromDimension().identifier().toString().toLowerCase(Locale.ROOT)
                                        );
                                    }
                                    return _builder.buildFuture();
                                }
                            )
                            .then(
                                Commands
                                    .argument("to_level", DimensionArgument.dimension() )
                                    .suggests(
                                        (_context, _builder) -> {
                                            Block frameMaterial = getPriorArgument(_context, "material", BlockInput.class).getState().getBlock();
                                            Block ignitionMaterial = getPriorArgument(_context, "igniter", BlockInput.class).getState().getBlock();
                                            Identifier fromLevel = getPriorArgument(_context, "from_level", Identifier.class);

                                            for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
                                                if(
                                                       !definition.frameMaterial().equals(frameMaterial)
                                                    || !definition.ignitionMaterial().equals(ignitionMaterial)
                                                    || !definition.fromDimension().identifier().equals(fromLevel)
                                                ) {
                                                    continue;
                                                }
                                                _builder.suggest(
                                                    definition.toDimension().identifier().toString().toLowerCase(Locale.ROOT)
                                                );
                                            }
                                            return _builder.buildFuture();
                                        }
                                    )
                                    .then(
                                        Commands
                                            .argument("type", StringArgumentType.word() )
                                            .suggests(
                                                (_context, _builder) -> {
                                                    Block frameMaterial = getPriorArgument(_context, "material", BlockInput.class).getState().getBlock();
                                                    Block ignitionMaterial = getPriorArgument(_context, "igniter", BlockInput.class).getState().getBlock();
                                                    Identifier fromLevel = getPriorArgument(_context, "from_level", Identifier.class);
                                                    Identifier toLevel = getPriorArgument(_context, "to_level", Identifier.class);
                                                    for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
                                                        if(
                                                               !definition.frameMaterial().equals(frameMaterial)
                                                            || !definition.ignitionMaterial().equals(ignitionMaterial)
                                                            || !definition.fromDimension().identifier().equals(fromLevel)
                                                            || !definition.toDimension().identifier().equals(toLevel)
                                                        ) {
                                                            continue;
                                                        }
                                                        _builder.suggest(
                                                            definition.type().name().toLowerCase(Locale.ROOT)
                                                        );
                                                    }
                                                    return _builder.buildFuture();
                                                }
                                            )
                                            .executes(WindowCommands::remove)
                                    )
                            )
                    )
            )
        ;
    }
    private static <T> T getPriorArgument(
        CommandContext<?> context,
        String name,
        Class<T> type
    ) {
        CommandContext<?> current = context;

        while(current != null) {
            try {
                return current.getArgument(name, type);
            } catch (IllegalArgumentException ignored) {
                current = current.getChild();
            }
        }

        throw new IllegalArgumentException(
            "No argument '" + name + "' found in command context"
        );
    }

    private static int link(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Block material = BlockStateArgument.getBlock(context, "material").getState().getBlock();
        Block igniter = BlockStateArgument.getBlock(context, "igniter").getState().getBlock();

        ServerLevel fromLevel = DimensionArgument.getDimension(context, "from_level");
        ServerLevel   tolevel = DimensionArgument.getDimension(context, "to_level");

        LinkType type = LinkType.valueOf(
            StringArgumentType.getString(context, "type").toUpperCase(Locale.ROOT)
        );
        DyeColor color = DyeColor.valueOf(
            StringArgumentType.getString(context, "color").toUpperCase()
        );

        PortalDefinition potentialDefinition = new PortalDefinition(
            material, igniter,
            fromLevel.dimension(), tolevel.dimension(),
            type, color
        );
        //Prevent Duplicates
        for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
            if( definition.equals(potentialDefinition) ) {
                context.getSource().sendFailure(
                    Component.translatable("command.window.duplicate_link")
                );
                return -1;
            }
        }

        WindowConfig.INSTANCE.definitions.add(potentialDefinition);
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

    private static int remove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Block frameMaterial = context.getArgument("material", BlockInput.class).getState().getBlock();
        Block ignitionMaterial = context.getArgument("igniter", BlockInput.class).getState().getBlock();
        ResourceKey<Level> fromLevel = DimensionArgument.getDimension(context, "from_level").dimension();
        ResourceKey<Level>   tolevel = DimensionArgument.getDimension(context, "to_level").dimension();
        LinkType type = LinkType.valueOf(
            StringArgumentType.getString(context, "type").toUpperCase(Locale.ROOT)
        );

        PortalDefinition commandPortalDefinition = new PortalDefinition(
            frameMaterial, ignitionMaterial,
            fromLevel, tolevel,
            type, null
        );

        for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
            if( !definition.equals(commandPortalDefinition) ) {
                continue;
            }

            context.getSource().sendSuccess(
                new Supplier<Component>() {
                    @Override
                    public Component get() {
                        return Component.translatable("command.window.removal_success");
                    }
                },
        false
            );
            WindowConfig.INSTANCE.definitions.remove(definition);
            return 0;
        }
        context.getSource().sendFailure(
            Component.translatable("command.window.removal_failure")
        );
        return 1;
    }
}
