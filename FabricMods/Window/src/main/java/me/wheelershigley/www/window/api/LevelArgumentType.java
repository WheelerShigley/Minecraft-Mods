package me.wheelershigley.www.window.api;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.concurrent.CompletableFuture;

import static me.wheelershigley.www.window.Window.getWindowIdentifier;

public class LevelArgumentType implements ArgumentType< ResourceKey<Level> > {
    public static void register() {
        ArgumentTypeRegistry.registerArgumentType(
            getWindowIdentifier("level"),
            LevelArgumentType.class,
            SingletonArgumentInfo.contextFree(LevelArgumentType::new)
        );
    }

    @Override
    public ResourceKey<Level> parse(StringReader reader) throws CommandSyntaxException {
        Identifier identifier = Identifier.read(reader);
        return ResourceKey.create(Registries.DIMENSION, identifier);
    }

    public static <S> CompletableFuture<Suggestions> listStaticSuggestions(
        final CommandContext<S> context, final SuggestionsBuilder builder
    ) {
        if( !(context.getSource() instanceof CommandSourceStack source) ) {
            return Suggestions.empty();
        }

        for(ResourceKey<Level> level : source.getServer().levelKeys() ) {
            builder.suggest( level.identifier().toString() );
        }
        return builder.buildFuture();
    }
}
