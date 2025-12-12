package me.wheelershigley.tree_in_a_forest.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.wheelershigley.tree_in_a_forest.blacklist.Blacklist;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class BlacklistedPlayersSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(
        CommandContext<ServerCommandSource> context,
        SuggestionsBuilder builder
    ) {
        for(String name : Blacklist.getBlacklistedNames() ) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    }
}
