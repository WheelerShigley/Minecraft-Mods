package me.wheelershigley.www.window.api;

import com.google.common.collect.Maps;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.DyeColor;

import java.util.Map;

public class CustomPoiTypes {
    public static ResourceKey<PoiType> WHITE_PORTAL      = createKey("white_portal"     );
    public static ResourceKey<PoiType> LIGHT_GRAY_PORTAL = createKey("light_gray_portal");
    public static ResourceKey<PoiType> GRAY_PORTAL       = createKey("gray_portal"      );
    public static ResourceKey<PoiType> BLACK_PORTAL      = createKey("black_portal"     );
    public static ResourceKey<PoiType> BROWN_PORTAL      = createKey("brown_portal"     );
    public static ResourceKey<PoiType> RED_PORTAL        = createKey("red_portal"       );
    public static ResourceKey<PoiType> ORANGE_PORTAL     = createKey("orange_portal"    );
    public static ResourceKey<PoiType> YELLOW_PORTAL     = createKey("yellow_portal"    );
    public static ResourceKey<PoiType> LIME_PORTAL       = createKey("lime_portal"      );
    public static ResourceKey<PoiType> GREEN_PORTAL      = createKey("green_portal"     );
    public static ResourceKey<PoiType> CYAN_PORTAL       = createKey("cyan_portal"      );
    public static ResourceKey<PoiType> LIGHT_BLUE_PORTAL = createKey("light_blue_portal");
    public static ResourceKey<PoiType> BLUE_PORTAL       = createKey("blue_portal"      );
    public static ResourceKey<PoiType> PURPLE_PORTAL     = createKey("purple_portal"    );
    public static ResourceKey<PoiType> MAGENTA_PORTAL    = createKey("magenta_portal"   );
    public static ResourceKey<PoiType> PINK_PORTAL       = createKey("pink_portal"      );

    public static Map< DyeColor, ResourceKey<PoiType> > portalPOIs; static {
        portalPOIs = Maps.newHashMap();

        portalPOIs.put(DyeColor.WHITE,      WHITE_PORTAL     );
        portalPOIs.put(DyeColor.LIGHT_GRAY, LIGHT_GRAY_PORTAL);
        portalPOIs.put(DyeColor.GRAY,       GRAY_PORTAL      );
        portalPOIs.put(DyeColor.BLACK,      BLACK_PORTAL     );
        portalPOIs.put(DyeColor.RED,        RED_PORTAL       );
        portalPOIs.put(DyeColor.BROWN,      BROWN_PORTAL     );
        portalPOIs.put(DyeColor.ORANGE,     ORANGE_PORTAL    );
        portalPOIs.put(DyeColor.YELLOW,     YELLOW_PORTAL    );
        portalPOIs.put(DyeColor.LIME,       LIME_PORTAL      );
        portalPOIs.put(DyeColor.GREEN,      GREEN_PORTAL     );
        portalPOIs.put(DyeColor.CYAN,       CYAN_PORTAL      );
        portalPOIs.put(DyeColor.LIGHT_BLUE, LIGHT_BLUE_PORTAL);
        portalPOIs.put(DyeColor.BLUE,       BLUE_PORTAL      );
        portalPOIs.put(DyeColor.PURPLE,     PURPLE_PORTAL    );
        portalPOIs.put(DyeColor.MAGENTA,    MAGENTA_PORTAL   );
        portalPOIs.put(DyeColor.PINK,       PINK_PORTAL      );
    }

    private static ResourceKey<PoiType> createKey(final String name) {
        return ResourceKey.create(
            Registries.POINT_OF_INTEREST_TYPE,
            Identifier.withDefaultNamespace(name)
        );
    }
}
