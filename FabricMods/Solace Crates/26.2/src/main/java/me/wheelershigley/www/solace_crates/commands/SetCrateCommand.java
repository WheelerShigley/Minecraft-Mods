package me.wheelershigley.www.solace_crates.commands;

import com.mojang.brigadier.context.CommandContext;
import me.wheelershigley.www.solace_crates.Permissions.PermissionsHelper;
import me.wheelershigley.www.solace_crates.SolaceCrates;
import me.wheelershigley.www.solace_crates.blocks.CrateBlockEntity;
import me.wheelershigley.www.solace_crates.registry.CrateRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.block.entity.BlockEntity;

import static me.wheelershigley.www.solace_crates.SolaceCrates.getTranslatedSolaceCrateComponent;

public class SetCrateCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                dispatcher.register(
                    Commands.literal(SolaceCrates.MOD_ID)
                        .then(
                            Commands.literal("set_crate")
                                .then(
                                    Commands.argument(
                                        "position",
                                        BlockPosArgument.blockPos()
                                    )
                                    .then(
                                        Commands.argument(
                                        "crate",
                                            IdentifierArgument.id()
                                        )
                                        .suggests( new CrateIdentifierSuggestionProvider() )
                                        .executes(SetCrateCommand::setCrate)
                                        .requires(PermissionsHelper::hasCommandPermission)
                                    )
                                )
                        )
                );
            }
        );
    }

    private static int setCrate(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if(player == null) {
            return 1;
        }

        ServerLevel level = context.getSource().getLevel();
        BlockPos pos = BlockPosArgument.getBlockPos(context, "position");
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if( !(blockEntity instanceof CrateBlockEntity crateBlockEntity) ) {
            player.sendSystemMessage(
                getTranslatedSolaceCrateComponent("solace_crates.command.set_crate.not_crate")
            );
            return 0;
        }

        Identifier crateIdentifier = IdentifierArgument.getId(context, "crate");
        if( !CrateRegistry.contains(crateIdentifier) ) {
            player.sendSystemMessage(
                getTranslatedSolaceCrateComponent(
                    "solace_crates.command.set_crate.identifier_missing",
                    crateIdentifier.toString()
                )
            );
            // does not return
        }

        crateBlockEntity.setCrateIdentifier(crateIdentifier);
        return 0;
    }
}
