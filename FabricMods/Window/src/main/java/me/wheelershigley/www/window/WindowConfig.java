package me.wheelershigley.www.window;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class WindowConfig {
    public static final WindowConfig INSTANCE = new WindowConfig();
    public Map<Identifier, Identifier> blockToLevel = new HashMap<>();

    public WindowConfig() {}
    public WindowConfig(Map<Identifier, Identifier> blockToLevel) {
        this.blockToLevel = blockToLevel;
    }

    public static final Codec<WindowConfig> CODEC = RecordCodecBuilder.create(
        (instance) -> {
            return instance.group(
                Codec
                    .unboundedMap(
                        Identifier.CODEC,
                        Identifier.CODEC
                    )
                    .fieldOf("block_to_level")
                    .forGetter(config -> config.blockToLevel)
            ).apply(instance, WindowConfig::new);
        }
    );

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        String label = Component.translatable("command.window.link_title").getString();
        label = label.substring(0, label.length() - 1);
        builder.append(label);

        for( Map.Entry<Identifier, Identifier> entry : blockToLevel.entrySet() ) {
            builder
                .append("\n    ")
                .append( entry.getKey() )
                .append(" => ")
                .append( entry.getValue() )
            ;
        }

        if( blockToLevel.isEmpty() ) {
            String empty = Component.translatable("command.window.empty").getString();
            builder.append("\n    ").append(empty);
        }

        return builder.toString();
    }
}