package me.wheelershigley.www.solace_fishing.menus;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public abstract class ImmutableChestMenu {
    //TODO: sub-menus
    //private HashMap<Integer, ImmutableChestMenu> subMenus = new HashMap<>();

    private MenuProvider getMenuProvider(Container container) {
        return new MenuProvider() {
            @Override
            public @NonNull Component getDisplayName() {
                return Component.translatable( getTranslationKey() );
            }

            @Override
            public @NonNull AbstractContainerMenu createMenu(
                int containerId,
                @NonNull Inventory inventory,
                @NonNull Player player
            ) {
                return new ChestMenu(
                    getMenuType(),
                    containerId, inventory,
                    container,
                    getMenuHeight()
                ) {
                    @Override
                    public void clicked(
                        final int slotIndex,
                        final int buttonNum,
                        final @NonNull ContainerInput containerInput,
                        final @NonNull Player player
                    ) {}

                    @Override
                    public @NonNull ItemStack quickMoveStack(
                        @NonNull Player player,
                        int slotId
                    ) {
                        return ItemStack.EMPTY;
                    }
                };
            }
        };
    }

    public static void open(
        Player player,
        ImmutableChestMenu menu
    ) {
        player.openMenu(
            menu.getMenuProvider( menu.getContainer() )
        );
    }

    private int getMenuHeight() {
        MenuType<ChestMenu> menuType = getMenuType();

        if(menuType == MenuType.GENERIC_9x1) { return 1; }
        if(menuType == MenuType.GENERIC_9x2) { return 2; }
        if(menuType == MenuType.GENERIC_9x3) { return 3; }
        if(menuType == MenuType.GENERIC_9x4) { return 4; }
        if(menuType == MenuType.GENERIC_9x5) { return 5; }
        if(menuType == MenuType.GENERIC_9x6) { return 6; }

        throw new IllegalStateException("Not a chest menu");
    }

    public abstract String getTranslationKey();
    public abstract MenuType<ChestMenu> getMenuType();
    public abstract Container getContainer();
}
