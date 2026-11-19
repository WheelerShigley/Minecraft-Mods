package me.wheelershigley.www.solace_crates.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.wheelershigley.www.solace_crates.crate.CrateDefinition;
import me.wheelershigley.www.solace_crates.registry.CrateRegistry;
import net.minecraft.commands.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

public class CrateIdentifierSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(
        CommandContext<CommandSourceStack> context,
        SuggestionsBuilder builder
    ) throws CommandSyntaxException {
        for(CrateDefinition definition : CrateRegistry.values() ) {
            builder.suggest( definition.IDENTIFIER().toString() );
        }
        return builder.buildFuture();
    }
}
