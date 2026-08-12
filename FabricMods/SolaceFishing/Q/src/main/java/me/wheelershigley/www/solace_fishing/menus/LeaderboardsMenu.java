package me.wheelershigley.www.solace_fishing.menus;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class LeaderboardsMenu extends ImmutableSimpleGui {
    private static final MenuType<?> MENU_TYPE = MenuType.GENERIC_9x6;

    public LeaderboardsMenu(ServerPlayer player, @Nullable ImmutableSimpleGui parent) {
        super(player, MENU_TYPE, parent);
    }

    @Override
    public String getTranslationKey() {
        return "solace_fishing.leaderboard_menu.title";
    }

    @Override
    public MenuType<?> getMenuType() {
        return LeaderboardsMenu.MENU_TYPE;
    }

    @Override
    public void initializeMenu() {}
}
