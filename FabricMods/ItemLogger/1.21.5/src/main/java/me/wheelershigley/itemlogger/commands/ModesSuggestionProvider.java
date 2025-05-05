package me.wheelershigley.itemlogger.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.wheelershigley.itemlogger.modes.Mode;
import me.wheelershigley.itemlogger.modes.Modes;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.concurrent.CompletableFuture;

public class ModesSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(
        CommandContext<FabricClientCommandSource> context,
        SuggestionsBuilder builder
    ) throws CommandSyntaxException {
        for(Mode mode : Modes.modes) {
            builder.suggest( Modes.toString(mode) );
        }
        return builder.buildFuture();
    }
}
