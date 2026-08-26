package me.wheelershigley.www.window;

import me.wheelershigley.www.window.portal.PortalBlockEntity;
import me.wheelershigley.www.window.registrations.WindowBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static me.wheelershigley.www.window.BlockItemIds.*;
import static me.wheelershigley.www.window.Window.getWindowIdentifier;

public class WindowBlockEntities {
    public static void staticInitialize() {}

    public static final BlockEntityType<PortalBlockEntity>
        WHITE_PORTAL =      registerPortal(WHITE_PORTAL_PATH),
        LIGHT_GRAY_PORTAL = registerPortal(LIGHT_GRAY_PORTAL_PATH),
        GRAY_PORTAL =       registerPortal(GRAY_PORTAL_PATH),
        BLACK_PORTAL =      registerPortal(BLACK_PORTAL_PATH),
        BROWN_PORTAL =      registerPortal(BROWN_PORTAL_PATH),
        RED_PORTAL =        registerPortal(RED_PORTAL_PATH),
        ORANGE_PORTAL =     registerPortal(ORANGE_PORTAL_PATH),
        YELLOW_PORTAL =     registerPortal(YELLOW_PORTAL_PATH),
        LIME_PORTAL =       registerPortal(LIME_PORTAL_PATH),
        GREEN_PORTAL =      registerPortal(GREEN_PORTAL_PATH),
        CYAN_PORTAL =       registerPortal(CYAN_PORTAL_PATH),
        LIGHT_BLUE_PORTAL = registerPortal(LIGHT_BLUE_PORTAL_PATH),
        BLUE_PORTAL =       registerPortal(BLUE_PORTAL_PATH),
        PURPLE_PORTAL =     registerPortal(PURPLE_PORTAL_PATH),
        MAGENTA_PORTAL =    registerPortal(MAGENTA_PORTAL_PATH),
        PINK_PORTAL =       registerPortal(PINK_PORTAL_PATH)
    ;

    public static BlockEntityType<PortalBlockEntity> registerPortal(String path) {
        return Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            getWindowIdentifier(path),
            FabricBlockEntityTypeBuilder.create(
                PortalBlockEntity::new,
                WindowBlocks.WHITE_PORTAL
            ).build()
        );
    }
}
