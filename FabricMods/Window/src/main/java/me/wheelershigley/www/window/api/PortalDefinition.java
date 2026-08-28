package me.wheelershigley.www.window.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record PortalDefinition(
    Block frameMaterial,
    Block ignitionMaterial,
    ResourceKey<Level> fromDimension,
    ResourceKey<Level>   toDimension,
    DyeColor color
) {
    public static final Codec<PortalDefinition> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec()
                .fieldOf("frame_material")
                .forGetter(PortalDefinition::frameMaterial)
            ,

            BuiltInRegistries.BLOCK.byNameCodec()
                .fieldOf("ignition_material")
                .forGetter(PortalDefinition::ignitionMaterial)
            ,

            ResourceKey.codec(Registries.DIMENSION)
                .fieldOf("from_dimension")
                .forGetter(PortalDefinition::fromDimension)
            ,

            ResourceKey.codec(Registries.DIMENSION)
                .fieldOf("to_dimension")
                .forGetter(PortalDefinition::toDimension)
            ,

            DyeColor.CODEC
                .fieldOf("color")
                .forGetter(PortalDefinition::color)
        ).apply(instance, PortalDefinition::new)
    );

    @Override
    public String toString() {
        return
            BuiltInRegistries.BLOCK.getKey(frameMaterial) +
            " + " +
            BuiltInRegistries.BLOCK.getKey(ignitionMaterial) +
            ":\n" +
            fromDimension.identifier() +
            " <=> " +
            toDimension.identifier() +
            " (" +
            color.getSerializedName() +
            ")"
        ;
    }
}
