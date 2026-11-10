package me.wheelershigley.www.solace_jobs.compatibility;

import me.wheelershigley.www.dandelion.registerations.ModItems;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.Item;

public class DandelionCompatibility implements Compatibility {
    @Override
    public boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("dandelion");
    }

    public Item getOrDefault(
        DandelionIcon icon,
        boolean isGreen,
        Item defaultItem
    ) {
        if( !isLoaded() ) {
            return defaultItem;
        }

        switch(icon) {
            case SWORD -> {
                return isGreen ? ModItems.GREEN_SWORD_ICON : ModItems.RED_SWORD_ICON;
            }
            case PICKAXE -> {
                return isGreen ? ModItems.GREEN_PICKAXE_ICON : ModItems.RED_PICKAXE_ICON;
            }
            case AXE -> {
                return isGreen ? ModItems.GREEN_AXE_ICON : ModItems.RED_AXE_ICON;
            }
            case SHOVEL -> {
                return isGreen ? ModItems.GREEN_SHOVEL_ICON : ModItems.RED_SHOVEL_ICON;
            }
            case HOE -> {
                return isGreen ? ModItems.GREEN_HOE_ICON : ModItems.RED_HOE_ICON;
            }
            case FISHING_ROD -> {
                return isGreen ? ModItems.GREEN_FISHING_ROD_ICON : ModItems.RED_FISHING_ROD_ICON;
            }
            case BRUSH -> {
                return isGreen ? ModItems.GREEN_BRUSH_ICON : ModItems.RED_BRUSH_ICON;
            }
            default -> {
                return defaultItem;
            }
        }
    }
}
