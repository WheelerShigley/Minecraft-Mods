package me.wheelershigley.www.window.registrations;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import me.wheelershigley.www.window.portal.PortalBlock;
import me.wheelershigley.www.window.portal.PortalBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static me.wheelershigley.www.window.BlockItemIds.*;
import static me.wheelershigley.www.window.Window.getWindowIdentifier;

public class WindowBlockEntities {
    public static void staticInitialize() {}

    public static final BlockEntityType<PortalBlockEntity>
        WHITE_PORTAL =      registerPortal(WHITE_PORTAL_PATH,       WindowBlocks.WHITE_PORTAL       ),
        LIGHT_GRAY_PORTAL = registerPortal(LIGHT_GRAY_PORTAL_PATH,  WindowBlocks.LIGHT_GRAY_PORTAL  ),
        GRAY_PORTAL =       registerPortal(GRAY_PORTAL_PATH,        WindowBlocks.GRAY_PORTAL        ),
        BLACK_PORTAL =      registerPortal(BLACK_PORTAL_PATH,       WindowBlocks.BLACK_PORTAL       ),
        BROWN_PORTAL =      registerPortal(BROWN_PORTAL_PATH,       WindowBlocks.BROWN_PORTAL       ),
        RED_PORTAL =        registerPortal(RED_PORTAL_PATH,         WindowBlocks.RED_PORTAL         ),
        ORANGE_PORTAL =     registerPortal(ORANGE_PORTAL_PATH,      WindowBlocks.ORANGE_PORTAL      ),
        YELLOW_PORTAL =     registerPortal(YELLOW_PORTAL_PATH,      WindowBlocks.YELLOW_PORTAL      ),
        LIME_PORTAL =       registerPortal(LIME_PORTAL_PATH,        WindowBlocks.LIME_PORTAL        ),
        GREEN_PORTAL =      registerPortal(GREEN_PORTAL_PATH,       WindowBlocks.GREEN_PORTAL       ),
        CYAN_PORTAL =       registerPortal(CYAN_PORTAL_PATH,        WindowBlocks.CYAN_PORTAL        ),
        LIGHT_BLUE_PORTAL = registerPortal(LIGHT_BLUE_PORTAL_PATH,  WindowBlocks.LIGHT_BLUE_PORTAL  ),
        BLUE_PORTAL =       registerPortal(BLUE_PORTAL_PATH,        WindowBlocks.BLUE_PORTAL        ),
        PURPLE_PORTAL =     registerPortal(PURPLE_PORTAL_PATH,      WindowBlocks.PURPLE_PORTAL      ),
        MAGENTA_PORTAL =    registerPortal(MAGENTA_PORTAL_PATH,     WindowBlocks.MAGENTA_PORTAL     ),
        PINK_PORTAL =       registerPortal(PINK_PORTAL_PATH,        WindowBlocks.PINK_PORTAL        )
    ;

    public static BlockEntityType<PortalBlockEntity> registerPortal(String path, Block block) {
        return Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            getWindowIdentifier(path),
            FabricBlockEntityTypeBuilder.create(
                (pos, state) -> {
                    DyeColor color = ( (PortalBlock)state.getBlock() ).COLOR;
                    return new PortalBlockEntity(pos, state, color);
                },
                block
            ).build()
        );
    }

    public static void registerBlockEntities() {
        PolymerBlockUtils.registerBlockEntity(WHITE_PORTAL);
        PolymerBlockUtils.registerBlockEntity(LIGHT_GRAY_PORTAL);
        PolymerBlockUtils.registerBlockEntity(GRAY_PORTAL);
        PolymerBlockUtils.registerBlockEntity(BLACK_PORTAL);
        PolymerBlockUtils.registerBlockEntity(BROWN_PORTAL);
        PolymerBlockUtils.registerBlockEntity(RED_PORTAL);
        PolymerBlockUtils.registerBlockEntity(ORANGE_PORTAL);
        PolymerBlockUtils.registerBlockEntity(YELLOW_PORTAL);
        PolymerBlockUtils.registerBlockEntity(LIME_PORTAL);
        PolymerBlockUtils.registerBlockEntity(GREEN_PORTAL);
        PolymerBlockUtils.registerBlockEntity(CYAN_PORTAL);
        PolymerBlockUtils.registerBlockEntity(LIGHT_BLUE_PORTAL);
        PolymerBlockUtils.registerBlockEntity(BLUE_PORTAL);
        PolymerBlockUtils.registerBlockEntity(PURPLE_PORTAL);
        PolymerBlockUtils.registerBlockEntity(MAGENTA_PORTAL);
        PolymerBlockUtils.registerBlockEntity(PINK_PORTAL);
    }
}
