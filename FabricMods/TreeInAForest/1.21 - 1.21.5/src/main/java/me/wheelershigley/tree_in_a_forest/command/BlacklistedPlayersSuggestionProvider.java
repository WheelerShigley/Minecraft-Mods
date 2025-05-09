package me.wheelershigley.tree_in_a_forest.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.wheelershigley.tree_in_a_forest.blacklist.Blacklist;
import me.wheelershigley.tree_in_a_forest.TreeInAForest;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class BlacklistedPlayersSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(
        CommandContext<ServerCommandSource> context,
        SuggestionsBuilder builder
    ) {
        String currentName;
        for(GameProfile profile : Blacklist.getBlackListedUsers() ) {
            TreeInAForest.LOGGER.info( profile.toString() );
            currentName = profile.getName();
            TreeInAForest.LOGGER.info( "a: "+ (currentName == null ? "null" : currentName));
            if(currentName == null) {
                continue;
            }
            builder.suggest(currentName);
        }
        return builder.buildFuture();
    }
}
