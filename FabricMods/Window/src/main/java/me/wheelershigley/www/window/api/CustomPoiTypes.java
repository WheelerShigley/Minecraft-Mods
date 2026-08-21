package me.wheelershigley.www.window.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class CustomPoiTypes {
    public static ResourceKey<PoiType> CUSTOM_PORTAL = createKey("custom_portal");

    private static ResourceKey<PoiType> createKey(final String name) {
        return ResourceKey.create(
            Registries.POINT_OF_INTEREST_TYPE,
            Identifier.withDefaultNamespace(name)
        );
    }
}
