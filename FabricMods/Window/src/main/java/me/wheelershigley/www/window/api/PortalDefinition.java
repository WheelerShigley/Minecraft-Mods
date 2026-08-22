package me.wheelershigley.www.window.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record PortalDefinition(
    Block frameMaterial,
    Block ignitionMaterial,
    ResourceKey<Level> dimension
) {
    public static final Codec<PortalDefinition> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec()
                .fieldOf("frame_material")
                .forGetter(PortalDefinition::frameMaterial),

            BuiltInRegistries.BLOCK.byNameCodec()
                .fieldOf("ignition_material")
                .forGetter(PortalDefinition::ignitionMaterial),

            ResourceKey.codec(Registries.DIMENSION)
                .fieldOf("dimension")
                .forGetter(PortalDefinition::dimension)
        ).apply(instance, PortalDefinition::new)
    );

    @Override
    public String toString() {
        return
            BuiltInRegistries.BLOCK.getKey(frameMaterial) +
            " + " +
            BuiltInRegistries.BLOCK.getKey(ignitionMaterial) +
            " => " +
            dimension.identifier()
        ;
    }
}
