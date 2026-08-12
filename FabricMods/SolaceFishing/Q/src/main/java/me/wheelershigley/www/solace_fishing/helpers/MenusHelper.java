package me.wheelershigley.www.solace_fishing.helpers;

import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class MenusHelper {
    private static final int ROW_SIZE = 9;

    public static int sizeOf(MenuType<?> type) {

        if(type == MenuType.GENERIC_9x1) { return 1*ROW_SIZE; }
        if(type == MenuType.GENERIC_9x2) { return 2*ROW_SIZE; }
        if(type == MenuType.GENERIC_9x3) { return 3*ROW_SIZE; }
        if(type == MenuType.GENERIC_9x4) { return 4*ROW_SIZE; }
        if(type == MenuType.GENERIC_9x5) { return 5*ROW_SIZE; }
        if(type == MenuType.GENERIC_9x6) { return 6*ROW_SIZE; }

        throw new IllegalStateException("Not a chest menu");
    }

    public static MenuType<ChestMenu> getMinimumChestMenu(@Nullable Integer slot_count) {
        if(slot_count == null) {
            return MenuType.GENERIC_9x1;
        }

        if(slot_count <= 1*ROW_SIZE) { return MenuType.GENERIC_9x1; }
        if(slot_count <= 2*ROW_SIZE) { return MenuType.GENERIC_9x2; }
        if(slot_count <= 3*ROW_SIZE) { return MenuType.GENERIC_9x3; }
        if(slot_count <= 4*ROW_SIZE) { return MenuType.GENERIC_9x4; }
        if(slot_count <= 5*ROW_SIZE) { return MenuType.GENERIC_9x5; }
        return MenuType.GENERIC_9x6;
    }
}
