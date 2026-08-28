package me.wheelershigley.www.window.registrations;

import me.wheelershigley.www.window.BlockItemIds;
import me.wheelershigley.www.window.portal.PortalBlock;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.world.item.DyeColor.*;

public class WindowBlocks implements ModInitializer {
    public static void staticInitialize() {}

    @Override
    public void onInitialize() {
        // Force Initializations
        WHITE_PORTAL.getClass();
        LIGHT_GRAY_PORTAL.getClass();
        GRAY_PORTAL.getClass();
        BLACK_PORTAL.getClass();
        BROWN_PORTAL.getClass();
        RED_PORTAL.getClass();
        ORANGE_PORTAL.getClass();
        YELLOW_PORTAL.getClass();
        LIME_PORTAL.getClass();
        GREEN_PORTAL.getClass();
        CYAN_PORTAL.getClass();
        LIGHT_BLUE_PORTAL.getClass();
        BLUE_PORTAL.getClass();
        PURPLE_PORTAL.getClass();
        MAGENTA_PORTAL.getClass();
        PINK_PORTAL.getClass();
    }

    public static final Block
        WHITE_PORTAL        = registerPortal(BlockItemIds.WHITE_PORTAL,         WHITE       ),
        LIGHT_GRAY_PORTAL   = registerPortal(BlockItemIds.LIGHT_GRAY_PORTAL,    LIGHT_GRAY  ),
        GRAY_PORTAL         = registerPortal(BlockItemIds.GRAY_PORTAL,          GRAY        ),
        BLACK_PORTAL        = registerPortal(BlockItemIds.BLACK_PORTAL,         BLACK       ),
        BROWN_PORTAL        = registerPortal(BlockItemIds.BROWN_PORTAL,         BROWN       ),
        RED_PORTAL          = registerPortal(BlockItemIds.RED_PORTAL,           RED         ),
        ORANGE_PORTAL       = registerPortal(BlockItemIds.ORANGE_PORTAL,        ORANGE      ),
        YELLOW_PORTAL       = registerPortal(BlockItemIds.YELLOW_PORTAL,        YELLOW      ),
        LIME_PORTAL         = registerPortal(BlockItemIds.LIME_PORTAL,          LIME        ),
        GREEN_PORTAL        = registerPortal(BlockItemIds.GREEN_PORTAL,         GREEN       ),
        CYAN_PORTAL         = registerPortal(BlockItemIds.CYAN_PORTAL,          CYAN        ),
        LIGHT_BLUE_PORTAL   = registerPortal(BlockItemIds.LIGHT_BLUE_PORTAL,    LIGHT_BLUE  ),
        BLUE_PORTAL         = registerPortal(BlockItemIds.BLUE_PORTAL,          BLUE        ),
        PURPLE_PORTAL       = registerPortal(BlockItemIds.PURPLE_PORTAL,        PURPLE      ),
        MAGENTA_PORTAL      = registerPortal(BlockItemIds.MAGENTA_PORTAL,       MAGENTA     ),
        PINK_PORTAL         = registerPortal(BlockItemIds.PINK_PORTAL,          PINK        )
    ;

    public static final Map<DyeColor, Block> coloredPortals; static {
        coloredPortals = new HashMap<>();

        coloredPortals.put(WHITE,       WHITE_PORTAL        );
        coloredPortals.put(LIGHT_GRAY,  LIGHT_GRAY_PORTAL   );
        coloredPortals.put(GRAY,        GRAY_PORTAL         );
        coloredPortals.put(BLACK,       BLACK_PORTAL        );
        coloredPortals.put(BROWN,       BROWN_PORTAL        );
        coloredPortals.put(RED,         RED_PORTAL          );
        coloredPortals.put(ORANGE,      ORANGE_PORTAL       );
        coloredPortals.put(YELLOW,      YELLOW_PORTAL       );
        coloredPortals.put(LIME,        LIME_PORTAL         );
        coloredPortals.put(GREEN,       GREEN_PORTAL        );
        coloredPortals.put(CYAN,        CYAN_PORTAL         );
        coloredPortals.put(LIGHT_BLUE,  LIGHT_BLUE_PORTAL   );
        coloredPortals.put(BLUE,        BLUE_PORTAL         );
        coloredPortals.put(PURPLE,      PURPLE_PORTAL       );
        coloredPortals.put(MAGENTA,     MAGENTA_PORTAL      );
        coloredPortals.put(PINK,        PINK_PORTAL         );
    }

    private static Block registerPortal(ResourceKey<Block> key, DyeColor color) {
        Block block = new PortalBlock(
            BlockBehaviour.Properties.of()
                .setId(key)
                .noOcclusion()
                .noCollision()
            ,
            color
        );
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }
}
