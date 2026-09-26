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
    double scale,
    LinkType type,
    DyeColor color,
    boolean generates
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
            Codec.DOUBLE
                .fieldOf("scale")
                .forGetter(PortalDefinition::scale)
            ,
            LinkType.CODEC
                .fieldOf("type")
                .forGetter(PortalDefinition::type)
            ,
            DyeColor.CODEC
                .fieldOf("color")
                .forGetter(PortalDefinition::color)
            ,
            Codec.BOOL
                .fieldOf("generates")
                .forGetter(PortalDefinition::generates)
        ).apply(instance, PortalDefinition::new)
    );

    public boolean equals(PortalDefinition other) {
        boolean hasSameDimensions = (
            other.fromDimension.equals(fromDimension)
            && other.toDimension.equals(toDimension)
        );
        if( this.type.equals(LinkType.BIDIRECTIONAL) ) {
            hasSameDimensions = hasSameDimensions || (
                other.fromDimension.equals(toDimension)
                && other.toDimension.equals(fromDimension)
            );
        }

        boolean type_overlaps = type.overlaps(other.type) || other.type.overlaps(type);

        //color, scale, and generation are not considered in equality
        return
               hasSameDimensions
            && other.ignitionMaterial.equals(ignitionMaterial)
            && other.frameMaterial.equals(frameMaterial)
            && type_overlaps
        ;
    }

    public boolean isValidFrom(ResourceKey<Level> currentDimension) {
        if( this.fromDimension.equals(currentDimension) ) {
            return true;
        }
        if(
            this.type.equals(LinkType.BIDIRECTIONAL)
            && this.toDimension.equals(currentDimension)
        ) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return
            BuiltInRegistries.BLOCK.getKey(frameMaterial) +
            " + " +
            BuiltInRegistries.BLOCK.getKey(ignitionMaterial) +
            " =\n" +
            fromDimension.identifier() +
            " " + type.toOperator() + " " +
            scale + " * " + toDimension.identifier() +
            " (" +
            color.getSerializedName() +
            ")\n" +
            "[portal does " + (!this.generates ? "not " : "") + "generate]"
        ;
    }
}
