package me.wheelershigley.www.window;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.wheelershigley.www.window.api.LinkType;
import me.wheelershigley.www.window.api.PortalDefinition;
import me.wheelershigley.www.window.portal.PortalBlock;
import me.wheelershigley.www.window.portal.PortalBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

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

    public @Nullable PortalDefinition getDefinition(PortalBlockEntity portalBlockEntity) {
        Block frameMaterial = portalBlockEntity.getFrame();
        Block ignitionMaterial = portalBlockEntity.getIgniter();
        if(portalBlockEntity.getLevel() == null) {
            return null;
        }
        ResourceKey<Level> fromDimension = portalBlockEntity.getLevel().dimension();
        DyeColor color = ( (PortalBlock)portalBlockEntity.getBlockState().getBlock() ).COLOR;

        for(PortalDefinition definition : this.definitions) {
            if(    definition.frameMaterial().equals(frameMaterial)
                && definition.ignitionMaterial().equals(ignitionMaterial)
                && definition.color().equals(color)
            ) {
                boolean bi_directional = definition.type().equals(LinkType.BIDIRECTIONAL);
                if(
                    definition.fromDimension().equals(fromDimension)
                    || (
                        bi_directional
                        && definition.toDimension().equals(fromDimension)
                    )
                ) {
                    return definition;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        String label = Component.translatable("command.window.link_title").getString();
        label = label.substring(0, label.length() - 1);
        builder.append(label);

        int link_counter = 0;
        for(PortalDefinition definition : definitions) {
            if(0 < link_counter++) {
                builder.append("\n");
            }
            builder
                .append("\n    ")
                .append( definition.toString().replace("\n","\n    ") )
            ;
        }

        if( definitions.isEmpty() ) {
            String empty = Component.translatable("command.window.empty").getString();
            builder.append("\n    ").append(empty);
        }

        return builder.toString();
    }
}