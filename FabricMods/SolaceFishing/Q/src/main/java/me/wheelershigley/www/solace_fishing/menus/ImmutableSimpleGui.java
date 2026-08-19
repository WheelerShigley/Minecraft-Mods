package me.wheelershigley.www.solace_fishing.menus;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;

public abstract class ImmutableSimpleGui extends SimpleGui {
    public static final int ROW_LENGTH = 9;

    private final ImmutableSimpleGui parent;

    public abstract String getTranslationKey();
    public abstract MenuType<?> getMenuType();
    public abstract void initializeMenu();

    public ImmutableSimpleGui(ServerPlayer player, MenuType<?> menuType, ImmutableSimpleGui parent) {
        super(menuType, player, false);
        this.parent = parent;

        this.setTitle(  Component.translatable( getTranslationKey() )  );
        this.initializeMenu();
    }

    @Override
    public void afterRemoval() {
        if(parent != null) {
            parent.open();
        }
    }
}
