package me.wheelershigley.www.solace_jobs.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.wheelershigley.www.solace_jobs.data.employments.Employments;
import me.wheelershigley.www.solace_jobs.data.employments.EmploymentsCodec;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinitions;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsSuggestionProvider;
import me.wheelershigley.www.solace_jobs.menus.JobsGUI;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class JobsCommand {
    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                dispatcher
                    .register(
                        Commands.literal("jobs")
                        .executes(
                            (context) -> {
                                ServerPlayer player = context.getSource().getPlayer();
                                JobsGUI menu = new JobsGUI(player);
                                return (menu.open() ? 0 : 1);
                            }
                        )
                    .then(
                        Commands.literal("join")
                        .then(
                            Commands.argument(
                                "job_identifier",
                                    IdentifierArgument.id()
                            )
                            .suggests( new JobsSuggestionProvider() )
                            .executes(JobsCommand::JoinJobCommand)
                        )
                    )
                    .then(
                        Commands.literal("leave")
                        .then(
                            Commands.argument(
                                "job_identifier",
                                IdentifierArgument.id()
                            )
                            .suggests( new JobsSuggestionProvider() )
                            .executes(JobsCommand::LeaveJobCommand)
                        )
                    )
                );
            }
        );
    }

    private static int JoinJobCommand(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if(player == null) {
            return 1;
        }

        Identifier jobIdentifier = IdentifierArgument.getId(context, "job_identifier");
        JobDefinition job = JobsCodec.INSTANCE.getInstanceData().getJob(jobIdentifier);
        if(job == null) {
            return 1;
        }

        Employments.join(
            EmploymentsCodec.INSTANCE.getInstanceData(),
            player,
            job
        );
        return 0;
    }
    private static int LeaveJobCommand(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if(player == null) {
            return 1;
        }

        Identifier jobIdentifier = IdentifierArgument.getId(context, "job_identifier");
        JobDefinition job = JobsCodec.INSTANCE.getInstanceData().getJob(jobIdentifier);
        if(job == null) {
            return 1;
        }

        Employments.leave(
            EmploymentsCodec.INSTANCE.getInstanceData(),
            player,
            job
        );
        return 0;
    }
}
