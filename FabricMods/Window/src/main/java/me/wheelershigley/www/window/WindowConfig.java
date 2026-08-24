package me.wheelershigley.www.window;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.wheelershigley.www.window.api.PortalDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class WindowConfig {
    public static WindowConfig INSTANCE = new WindowConfig();
    public Set<PortalDefinition> definitions = new HashSet<>();

    public WindowConfig() {}
    public WindowConfig(Set<PortalDefinition> definitions) {
        this.definitions = definitions;
    }

    public static final Codec<WindowConfig> CODEC = RecordCodecBuilder.create(
        (instance) -> {
            return instance.group(
                Codec
                    .list(PortalDefinition.CODEC)
                    .xmap(
                        list -> (Set<PortalDefinition>)( new HashSet<>(list) ),
                        ArrayList::new
                    )
                    .fieldOf("definitions")
                    .forGetter(config -> config.definitions)
            ).apply(instance, WindowConfig::new);
        }
    );

    public static WindowConfig load(Path path) {
        if( !Files.exists(path) ) {
            WindowConfig config = new WindowConfig();
            config.save(path);
            return config;
        }

        try {
            String json = Files.readString(path);
            JsonElement element = JsonParser.parseString(json);

            return CODEC
                .parse(JsonOps.INSTANCE, element)
                .getOrThrow()
            ;
        } catch(Exception exception) {
            throw new RuntimeException("Failed to load Window config.", exception);
        }
    }

    public void save(Path path) {
        try {
            JsonElement element = CODEC
                .encodeStart(JsonOps.INSTANCE, this)
                .getOrThrow()
            ;

            Files.createDirectories( path.getParent() );
            Files.writeString(
                path,
                element.toString()
            );
        } catch(Exception exception) {
            throw new RuntimeException("Failed to save Window config.", exception);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        String label = Component.translatable("command.window.link_title").getString();
        label = label.substring(0, label.length() - 1);
        builder.append(label);

        for(PortalDefinition definition : definitions) {
            builder
                .append("\n    ")
                .append( definition.toString() )
            ;
        }

        if( definitions.isEmpty() ) {
            String empty = Component.translatable("command.window.empty").getString();
            builder.append("\n    ").append(empty);
        }

        return builder.toString();
    }
}