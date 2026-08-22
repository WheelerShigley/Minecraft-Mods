package me.wheelershigley.www.window;

import me.wheelershigley.www.window.portal.CustomPortalBlockEntity;
import me.wheelershigley.www.window.registrations.WindowBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static me.wheelershigley.www.window.Window.getWindowIdentifier;

public class WindowBlockEntities {
    public static void staticInitialize() {}

    public static final BlockEntityType<CustomPortalBlockEntity> CUSTOM_PORTAL = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        getWindowIdentifier("custom_portal"),
        FabricBlockEntityTypeBuilder.create(
            CustomPortalBlockEntity::new,
            WindowBlocks.CUSTOM_PORTAL_BLOCK
        ).build()
    );
}
