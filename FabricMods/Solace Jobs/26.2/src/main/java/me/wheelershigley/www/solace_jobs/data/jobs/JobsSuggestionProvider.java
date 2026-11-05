package me.wheelershigley.www.solace_jobs.data.jobs;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class JobsSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(
        CommandContext<CommandSourceStack> context,
        SuggestionsBuilder builder
    ) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        Set<JobDefinition> jobs = JobsCodec.INSTANCE.getInstanceData().getJobs();
        for(JobDefinition job : jobs) {
            builder.suggest( job.identifier.toString() );
        }

        return builder.buildFuture();
    }
}
