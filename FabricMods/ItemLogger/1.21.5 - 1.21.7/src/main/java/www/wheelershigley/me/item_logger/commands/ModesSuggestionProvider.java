package www.wheelershigley.me.item_logger.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import www.wheelershigley.me.item_logger.modes.Mode;
import www.wheelershigley.me.item_logger.modes.Modes;
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
