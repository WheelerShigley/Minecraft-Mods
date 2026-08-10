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
                return Component.translatable("solace_fishing.fishing_menu.title");
            }

            @Override
            public @NonNull AbstractContainerMenu createMenu(
                int containerId,
                @NonNull Inventory inventory,
                @NonNull Player player
            ) {
                return new ChestMenu(
                    MenuType.GENERIC_9x3,
                    containerId, inventory,
                    container,
                    3
                ) {
                    @Override
                    public void clicked(
                        final int slotIndex,
                        final int buttonNum,
                        final ContainerInput containerInput,
                        final Player player
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

    public abstract Container getContainer();
}
