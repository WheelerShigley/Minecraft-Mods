package me.wheelershigley.www.solace_crates.blocks;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import me.wheelershigley.www.solace_crates.SolaceCrates;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static void initialize() {
        registerPolymerBlockEntities();
    }

    public static final BlockEntityType<CrateBlockEntity> CRATE_BLOCK_ENTITY = register(
        CrateBlock.NAME,
        CrateBlockEntity::new,
        ModBlocks.CRATE
    );

    private static <T extends BlockEntity> BlockEntityType<T> register(
        String name,
        FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
        Block... blocks
    ) {
        Identifier identifier = SolaceCrates.id(name);
        return Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            identifier,
            FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build()
        );
    }

    private static void registerPolymerBlockEntities() {
        PolymerBlockUtils.registerBlockEntity(CRATE_BLOCK_ENTITY);
    }
}
