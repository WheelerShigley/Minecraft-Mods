package me.wheelershigley.www.solace_fishing.menus;

import eu.pb4.sgui.api.gui.SimpleGui;
import me.wheelershigley.www.solace_fishing.helpers.MenusHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import static net.minecraft.world.entity.player.Inventory.INVENTORY_SIZE;

public abstract class ImmutableSimpleGui extends SimpleGui {
    public static final int ROW_LENGTH = 9;

    private final ImmutableSimpleGui parent;

    public abstract String getTranslationKey();
    public abstract MenuType<?> getMenuType();
    public abstract void initializeMenu();

    public ImmutableSimpleGui(ServerPlayer player, MenuType<?> menuType, ImmutableSimpleGui parent) {
        super(menuType, player, true);
        this.parent = parent;

        this.setTitle(  Component.translatable( getTranslationKey() )  );
        this.initializeMenu();
        this.initializeInventory();
    }

    private void initializeInventory() {
        Inventory inventory = player.getInventory();
        final int MENU_SIZE = MenusHelper.sizeOf( this.getMenuType() );

        // Body
        for(int i = ROW_LENGTH; i < INVENTORY_SIZE+ROW_LENGTH; i++) {
            this.setSlot(MENU_SIZE+i-ROW_LENGTH, inventory.getItem(i) );
        }

        // Hotbar
        for(int i = 0; i < ROW_LENGTH; i++) {
            this.setSlot(MENU_SIZE+INVENTORY_SIZE+i-ROW_LENGTH, inventory.getItem(i) );
        }
    }

    @Override
    public void close(boolean skipSync) {
        System.out.println("a");
        /* Custom Addition*/
        if(this.parent != null) {
            System.out.println("b");
            parent.open();
            return;
        }
        System.out.println("c");

        /* SGUI Implementation */
        if(
            (this.isOpen() || skipSync)
            && !this.reOpen
        ) {
            if (!skipSync && this.player.containerMenu == this.wrappedMenu) {
                this.player.closeContainer();
                this.wrappedMenu = null;
            }

            this.player.containerMenu.sendAllDataToRemote();

            this.onManualClose();
        } else {
            this.reOpen = false;
        }
    }
}
