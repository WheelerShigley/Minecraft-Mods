package me.wheelershigley.www.window.registrations;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
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
    private static final String[]
        COMMAND_ROOT = {"window","win","mw"},
        TELEPORT = {"teleport","tp","goto"},
        LINK = {"link"}
    ;
    private static final String
        PLAYER = "player",
        LEVEL = "level",

        ADD = "add",
        REMOVE = "remove",
        LIST = "list",

        FRAME_MATERIAL = "frame_material",
        IGNITER_MATERIAL = "igniter_material",
        FROM_LEVEL_IDENTIFIER = "from_level_identifier",
        TO_LEVEL_IDENTIFIER = "to_level_identifier",
        SCALE = "scale",
        COLOR = "color",
        LINK_TYPE = "link_type"
    ;

    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, context, selection) -> {
                final LiteralCommandNode<CommandSourceStack> WINDOW_COMMAND = getWindowCommand(context);

                // Aliases
                for(int index = 1; index < COMMAND_ROOT.length; index++) {
                    dispatcher.getRoot().addChild(WINDOW_COMMAND);
                    dispatcher.register(
                        Commands
                            .literal(COMMAND_ROOT[index])
                            .redirect(WINDOW_COMMAND)
                            .executes( WINDOW_COMMAND.getCommand() )
                    );
                }
            }
        );
    }

    /* Sub-commands */

    private static LiteralCommandNode<CommandSourceStack> getWindowCommand(CommandBuildContext context) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands
            .literal(COMMAND_ROOT[0])
            .requires(WindowCommands::getWindowsCommandPermission)
        ;

        for(String commandlet : TELEPORT) {
            command = command.then( tpCommandlet(commandlet) );
        }
        for(String commandlet : LINK) {
            command = command.then( linkCommandlet(commandlet, context) );
        }

        return command.build();
    }

    private static ArgumentBuilder<CommandSourceStack, ?> tpCommandlet(String name) {
        return Commands.literal(name)
            .then(
                Commands
                    .argument(PLAYER, EntityArgument.player() )
                    .then(
                        Commands.argument(LEVEL, DimensionArgument.dimension() )
                    )
                    .executes(WindowCommands::teleport)
            )
        ;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> linkCommandlet(String name, CommandBuildContext context) {
        return Commands.literal(name)
            .requires(WindowCommands::getWindowsCommandPermission)
            .then(
                Commands.literal(ADD).then( addCommandlet(context) )
            )
            .then(
                Commands.literal(REMOVE).then( removeCommandlet(context) )
            )
            .then(
                Commands.literal(LIST).executes(WindowCommands::linksList)
            )
        ;
    }
    private static RequiredArgumentBuilder<CommandSourceStack, ?> addCommandlet(CommandBuildContext context) {
        return
            typeArgument(context, false).then(
                materialArgument(context, false).then(
                    igniterArgument(context, false).then(
                        fromLevelArgument(context, false).then(
                            toLevelArgument(context,  false).then(
                                scaleArgument(context, false).then(
                                    colorArgument(context, false).executes(WindowCommands::link)
                                )
                            )
                        )
                    )
                )
            )
        ;
    }
    private static RequiredArgumentBuilder<CommandSourceStack, ?> removeCommandlet(CommandBuildContext context) {
        return
            typeArgument(context, true).then(
                materialArgument(context, true).then(
                    igniterArgument(context, true).then(
                        fromLevelArgument(context, true).then(
                            toLevelArgument(context, true).executes(WindowCommands::remove)
                        )
                    )
                )
            )
        ;
    }

    /* Argument Abstractions */

    private static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(
        String name, ArgumentType<T> type,
        SuggestionProvider<CommandSourceStack> provider, boolean use_provider
    ) {
        RequiredArgumentBuilder<CommandSourceStack, T> command =
            Commands.argument(name, type)
        ;
        if(use_provider) {
            command = command.suggests(provider);
        }
        return command;
    }

    private static final SuggestionProvider<CommandSourceStack> LIMITED_TYPE_PROVIDER =
        (_context, _builder) -> {
            for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
                _builder.suggest(
                    definition.type().name().toLowerCase(Locale.ROOT)
                );
            }
            return _builder.buildFuture();
        }
    ;
    private static final SuggestionProvider<CommandSourceStack> UNLIMITED_TYPE_PROVIDER =
        (_context, _builder) -> {
            for( LinkType type : LinkType.values() ) {
                _builder.suggest(
                    type.name().toLowerCase(Locale.ROOT)
                );
            }
            return _builder.buildFuture();
        }
    ;
    private static final SuggestionProvider<CommandSourceStack> LIMITED_MATERIAL_PROVIDER =
        (_context, _builder) -> {
            String type_string = getPriorArgument(_context, LINK_TYPE, String.class).toLowerCase(Locale.ROOT);
            for (PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
                if( !definition.type().name().toLowerCase(Locale.ROOT).equals(type_string) ) {
                    continue;
                }
                _builder.suggest(
                    BuiltInRegistries.BLOCK.wrapAsHolder(definition.frameMaterial()).getRegisteredName()
                );
            }
            return _builder.buildFuture();
        }
    ;
    private static final SuggestionProvider<CommandSourceStack> LIMITED_IGNITER_PROVIDER =
        (_context, _builder) -> {
            String type_string = getPriorArgument(_context, LINK_TYPE, String.class).toLowerCase(Locale.ROOT);
            Block material = getPriorArgument(_context, FRAME_MATERIAL, BlockInput.class).getState().getBlock();
            for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
                if(    !definition.type().name().toLowerCase(Locale.ROOT).equals(type_string)
                    || !definition.frameMaterial().equals(material)
                ) {
                    continue;
                }
                _builder.suggest(
                        BuiltInRegistries.BLOCK.wrapAsHolder( definition.ignitionMaterial() ).getRegisteredName()
                );
            }
            return _builder.buildFuture();
        }
    ;
    private static final SuggestionProvider<CommandSourceStack> LIMITED_FROM_LEVEL_PROVIDER =
        (_context, _builder) -> {
            String type_string = getPriorArgument(_context, LINK_TYPE, String.class).toLowerCase(Locale.ROOT);
            Block frameMaterial = getPriorArgument(_context, FRAME_MATERIAL, BlockInput.class).getState().getBlock();
            Block ignitionMaterial = getPriorArgument(_context, IGNITER_MATERIAL, BlockInput.class).getState().getBlock();
            for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
                if(    !definition.type().name().toLowerCase(Locale.ROOT).equals(type_string)
                    || !definition.frameMaterial().equals(frameMaterial)
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
    ;
    private static final SuggestionProvider<CommandSourceStack> LIMITED_TO_LEVEL_PROVIDER =
        (_context, _builder) -> {
            String type_string = getPriorArgument(_context, LINK_TYPE, String.class).toLowerCase(Locale.ROOT);
            Block frameMaterial = getPriorArgument(_context, FRAME_MATERIAL, BlockInput.class).getState().getBlock();
            Block ignitionMaterial = getPriorArgument(_context, IGNITER_MATERIAL, BlockInput.class).getState().getBlock();
            Identifier fromLevel = getPriorArgument(_context, FROM_LEVEL_IDENTIFIER, Identifier.class);

            for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
                if(    !definition.type().name().toLowerCase(Locale.ROOT).equals(type_string)
                    || !definition.frameMaterial().equals(frameMaterial)
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
    ;
    private static final SuggestionProvider<CommandSourceStack> LIMITED_SCALE_PROVIDER =
        (_context, _builder) -> {
            String type_string = getPriorArgument(_context, LINK_TYPE, String.class).toLowerCase(Locale.ROOT);
            Block frameMaterial = getPriorArgument(_context, FRAME_MATERIAL, BlockInput.class).getState().getBlock();
            Block ignitionMaterial = getPriorArgument(_context, IGNITER_MATERIAL, BlockInput.class).getState().getBlock();
            Identifier fromLevel = getPriorArgument(_context, FROM_LEVEL_IDENTIFIER, Identifier.class);
            Identifier toLevel = getPriorArgument(_context, TO_LEVEL_IDENTIFIER, Identifier.class);

            for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
                if(    !definition.type().name().toLowerCase(Locale.ROOT).equals(type_string)
                    || !definition.frameMaterial().equals(frameMaterial)
                    || !definition.ignitionMaterial().equals(ignitionMaterial)
                    || !definition.fromDimension().identifier().equals(fromLevel)
                    || !definition.toDimension().identifier().equals(toLevel)
                ) {
                    continue;
                }
                _builder.suggest(
                    Double.toString( definition.scale() )
                );
            }
            return _builder.buildFuture();
        }
    ;
    private static final SuggestionProvider<CommandSourceStack> UNLIMITED_SCALE_PROVIDER =
        (_context, _builder) -> {
            _builder.suggest( Double.toString(1.0) );
            return _builder.buildFuture();
        }
    ;
    private static final SuggestionProvider<CommandSourceStack> LIMITED_COLOR_PROVIDER =
        (_context, _builder) -> {
            String type_string = getPriorArgument(_context, LINK_TYPE, String.class).toLowerCase(Locale.ROOT);
            Block frameMaterial = getPriorArgument(_context, FRAME_MATERIAL, BlockInput.class).getState().getBlock();
            Block ignitionMaterial = getPriorArgument(_context, IGNITER_MATERIAL, BlockInput.class).getState().getBlock();
            Identifier fromLevel = getPriorArgument(_context, FROM_LEVEL_IDENTIFIER, Identifier.class);
            Identifier toLevel = getPriorArgument(_context, TO_LEVEL_IDENTIFIER, Identifier.class);
            Double scale = getPriorArgument(_context, SCALE, Double.class);
            for(PortalDefinition definition : WindowConfig.INSTANCE.definitions) {
                if(    !definition.type().name().toLowerCase(Locale.ROOT).equals(type_string)
                    || !definition.frameMaterial().equals(frameMaterial)
                    || !definition.ignitionMaterial().equals(ignitionMaterial)
                    || !definition.fromDimension().identifier().equals(fromLevel)
                    || !definition.toDimension().identifier().equals(toLevel)
                    || definition.scale() != scale
                ) {
                    continue;
                }
                _builder.suggest(
                    definition.color().getName().toLowerCase(Locale.ROOT)
                );
            }
            return _builder.buildFuture();
        }
    ;
    private static final SuggestionProvider<CommandSourceStack> UNLIMITED_COLOR_PROVIDER =
        (_context, _builder) -> {
            for( DyeColor color : DyeColor.values() ) {
                _builder.suggest( color.getName() );
            }
            return _builder.buildFuture();
        }
    ;

    private static RequiredArgumentBuilder<CommandSourceStack, ?> materialArgument(
        CommandBuildContext context, boolean definition_limited
    ) {
        return argument(FRAME_MATERIAL, BlockStateArgument.block(context), LIMITED_MATERIAL_PROVIDER, definition_limited);
    }
    private static RequiredArgumentBuilder<CommandSourceStack, ?> igniterArgument(
        CommandBuildContext context, boolean definition_limited
    ) {
        return argument(IGNITER_MATERIAL, BlockStateArgument.block(context), LIMITED_IGNITER_PROVIDER, definition_limited);
    }
    private static RequiredArgumentBuilder<CommandSourceStack, ?> fromLevelArgument(
        CommandBuildContext context, boolean definition_limited
    ) {
        return argument(FROM_LEVEL_IDENTIFIER, DimensionArgument.dimension(), LIMITED_FROM_LEVEL_PROVIDER, definition_limited);
    }
    private static RequiredArgumentBuilder<CommandSourceStack, ?> toLevelArgument(
        CommandBuildContext context, boolean definition_limited
    ) {
        return argument(TO_LEVEL_IDENTIFIER, DimensionArgument.dimension(), LIMITED_TO_LEVEL_PROVIDER, definition_limited);
    }
    private static RequiredArgumentBuilder<CommandSourceStack, ?> scaleArgument(
            CommandBuildContext context, boolean definition_limited
    ) {
        SuggestionProvider<CommandSourceStack> provider = definition_limited ? LIMITED_SCALE_PROVIDER : UNLIMITED_SCALE_PROVIDER;
        return argument(SCALE, DoubleArgumentType.doubleArg(), provider, true);
    }
    private static RequiredArgumentBuilder<CommandSourceStack, ?> colorArgument(
        CommandBuildContext context, boolean definition_limited
    ) {
        SuggestionProvider<CommandSourceStack> provider = definition_limited ? LIMITED_COLOR_PROVIDER : UNLIMITED_COLOR_PROVIDER;
        return argument(COLOR, StringArgumentType.word(), provider, true);
    }
    private static RequiredArgumentBuilder<CommandSourceStack, ?> typeArgument(
        CommandBuildContext context, boolean definition_limited
    ) {
        SuggestionProvider<CommandSourceStack> provider = definition_limited ? LIMITED_TYPE_PROVIDER : UNLIMITED_TYPE_PROVIDER;
        return argument(LINK_TYPE, StringArgumentType.word(), provider, true);
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

    /* Command Executions */

    private static int teleport(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, PLAYER);
        ServerLevel serverLevel = DimensionArgument.getDimension(context, LEVEL);

        TeleportTransition transition = CustomPortal.getTransition(player, serverLevel);
        if(transition == null) {
            return -1;
        }
        player.teleport(transition);
        return 0;
    }

    private static int link(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Block material = BlockStateArgument.getBlock(context, FRAME_MATERIAL).getState().getBlock();
        Block igniter = BlockStateArgument.getBlock(context, IGNITER_MATERIAL).getState().getBlock();

        ServerLevel fromLevel = DimensionArgument.getDimension(context, FROM_LEVEL_IDENTIFIER);
        ServerLevel   tolevel = DimensionArgument.getDimension(context, TO_LEVEL_IDENTIFIER);
        double scale          = DoubleArgumentType.getDouble(context, SCALE);

        LinkType type = LinkType.valueOf(
            StringArgumentType.getString(context, LINK_TYPE).toUpperCase(Locale.ROOT)
        );
        DyeColor color = DyeColor.valueOf(
            StringArgumentType.getString(context, COLOR).toUpperCase()
        );

        PortalDefinition potentialDefinition = new PortalDefinition(
            material, igniter,
            fromLevel.dimension(), tolevel.dimension(), scale,
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

        context.getSource().sendSuccess(
            new Supplier<Component>() {
                @Override
                public Component get() {
                    return Component.translatable("command.window.link_success");
                }
            },
            false
        );
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
        Block frameMaterial = context.getArgument(FRAME_MATERIAL, BlockInput.class).getState().getBlock();
        Block ignitionMaterial = context.getArgument(IGNITER_MATERIAL, BlockInput.class).getState().getBlock();
        ResourceKey<Level> fromLevel = DimensionArgument.getDimension(context, FROM_LEVEL_IDENTIFIER).dimension();
        ResourceKey<Level>   tolevel = DimensionArgument.getDimension(context, TO_LEVEL_IDENTIFIER).dimension();
        LinkType type = LinkType.valueOf(
            StringArgumentType.getString(context, LINK_TYPE).toUpperCase(Locale.ROOT)
        );

        PortalDefinition commandPortalDefinition = new PortalDefinition(
            frameMaterial, ignitionMaterial,
            fromLevel, tolevel, 1.0,
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

    /* Miscellaneous */
    private static boolean getWindowsCommandPermission(CommandSourceStack source) {
        return source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR);
    }
}
