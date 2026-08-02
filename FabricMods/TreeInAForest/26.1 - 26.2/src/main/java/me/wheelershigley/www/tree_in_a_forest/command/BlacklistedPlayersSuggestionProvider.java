package me.wheelershigley.www.tree_in_a_forest.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.wheelershigley.www.tree_in_a_forest.blacklist.Blacklist;
import net.minecraft.commands.CommandSourceStack;
import java.util.concurrent.CompletableFuture;

public class BlacklistedPlayersSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(
        CommandContext<CommandSourceStack> context,
        SuggestionsBuilder builder
    ) {
        for(String name : Blacklist.getBlacklistedNames() ) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    }
}
