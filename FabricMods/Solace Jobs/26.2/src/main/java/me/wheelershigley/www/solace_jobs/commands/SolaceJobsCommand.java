package me.wheelershigley.www.solace_jobs.commands;

import com.mojang.brigadier.context.CommandContext;
import me.wheelershigley.www.solace_jobs.LuckPermsHelper;
import me.wheelershigley.www.solace_jobs.SolaceJobs;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Set;

public class SolaceJobsCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                dispatcher.register(
                    Commands.literal(SolaceJobs.MOD_ID)
                        .then(
                            Commands.literal("load_default_job_defitions")
                                .requires(
                                    (source) -> {
                                        return LuckPermsHelper.hasPermission(source, "solace_jobs.load_default_job_defitions");
                                    }
                                )
                                .executes(SolaceJobsCommand::loadDefaultJobDefinitions)
                        )
                );
            }
        );
    }

    private static int loadDefaultJobDefinitions(CommandContext<CommandSourceStack> context) {
        Set<JobDefinition> defaultJobs = JobsCodec.INSTANCE.getDefaultCodec().getJobs();
        JobsCodec.INSTANCE.getInstanceData().getData().clear();
        JobsCodec.INSTANCE.getInstanceData().getData().addAll(defaultJobs);
        return 0;
    }
}
