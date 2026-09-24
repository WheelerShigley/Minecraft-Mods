package me.wheelershigley.www.window;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import static me.wheelershigley.www.window.Window.getWindowIdentifier;

public class BlockItemIds {
    public static final String
        WHITE_PORTAL_PATH       = "white_portal",
        LIGHT_GRAY_PORTAL_PATH  = "light_gray_portal",
        GRAY_PORTAL_PATH        = "gray_portal",
        BLACK_PORTAL_PATH       = "black_portal",
        BROWN_PORTAL_PATH       = "brown_portal",
        RED_PORTAL_PATH         = "red_portal",
        ORANGE_PORTAL_PATH      = "orange_portal",
        YELLOW_PORTAL_PATH      = "yellow_portal",
        LIME_PORTAL_PATH        = "lime_portal",
        GREEN_PORTAL_PATH       = "green_portal",
        CYAN_PORTAL_PATH        = "cyan_portal",
        LIGHT_BLUE_PORTAL_PATH  = "light_blue_portal",
        BLUE_PORTAL_PATH        = "blue_portal",
        PURPLE_PORTAL_PATH      = "purple_portal",
        MAGENTA_PORTAL_PATH     = "magenta_portal",
        PINK_PORTAL_PATH        = "pink_portal"
    ;
    public static final ResourceKey<Block>
        WHITE_PORTAL        = createPortal(WHITE_PORTAL_PATH),
        LIGHT_GRAY_PORTAL   = createPortal(LIGHT_GRAY_PORTAL_PATH),
        GRAY_PORTAL         = createPortal(GRAY_PORTAL_PATH),
        BLACK_PORTAL        = createPortal(BLACK_PORTAL_PATH),
        BROWN_PORTAL        = createPortal(BROWN_PORTAL_PATH),
        RED_PORTAL          = createPortal(RED_PORTAL_PATH),
        ORANGE_PORTAL       = createPortal(ORANGE_PORTAL_PATH),
        YELLOW_PORTAL       = createPortal(YELLOW_PORTAL_PATH),
        LIME_PORTAL         = createPortal(LIME_PORTAL_PATH),
        GREEN_PORTAL        = createPortal(GREEN_PORTAL_PATH),
        CYAN_PORTAL         = createPortal(CYAN_PORTAL_PATH),
        LIGHT_BLUE_PORTAL   = createPortal(LIGHT_BLUE_PORTAL_PATH),
        BLUE_PORTAL         = createPortal(BLUE_PORTAL_PATH),
        PURPLE_PORTAL       = createPortal(PURPLE_PORTAL_PATH),
        MAGENTA_PORTAL      = createPortal(MAGENTA_PORTAL_PATH),
        PINK_PORTAL         = createPortal(PINK_PORTAL_PATH)
    ;

    private static ResourceKey<Block> createPortal(String path) {
        return ResourceKey.create(
            Registries.BLOCK,
            getWindowIdentifier(path)
        );
    }
}
