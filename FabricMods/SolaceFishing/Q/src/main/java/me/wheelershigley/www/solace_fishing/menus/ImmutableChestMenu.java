package me.wheelershigley.www.solace_fishing.menus;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;

@Deprecated
public abstract class ImmutableChestMenu extends ChestMenu {
    public ImmutableChestMenu(
        MenuType<?> menuType,
        int containerId, Inventory inventory, Container container, int rows
    ) {
        super(menuType, containerId, inventory, container, rows);
    }

    private ImmutableChestMenu parent;
    private final HashMap<Integer, ImmutableChestMenu> childrenMap = new HashMap<>();

    public abstract void addChildren();
    public abstract String getTranslationKey();
    public abstract MenuType<ChestMenu> getMenuType();
    public abstract Container getContainer();

    public static void open(
        Player player,
        ImmutableChestMenu menu
    ) {
        if( !(player instanceof ServerPlayer) ) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;

        // Establish the menu (on the server)
        serverPlayer.containerMenu = menu;

        serverPlayer.containerMenu.incrementStateId();
        serverPlayer.containerMenu.broadcastFullState();

        // Tell the client to open the menu with the same container ID
        serverPlayer.connection.send(
            new ClientboundOpenScreenPacket(
                menu.containerId,
                menu.getMenuType(),
                Component.translatable(menu.getTranslationKey())
            )
        );

        // Synchronize menu-contents
        serverPlayer.connection.send(
            new ClientboundContainerSetContentPacket(
                menu.containerId,
                menu.getStateId(),
                menu.getItems(),
                menu.getCarried()
            )
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

    public void setParent(ImmutableChestMenu parent) {
        this.parent = parent;
    }
    public ImmutableChestMenu getParent() {
        return this.parent;
    }

    void addChild(int index, ImmutableChestMenu child) {
        childrenMap.put(index, child);
    }

    //Enforce Immutability
    @Override
    public void clicked(
        final int slotIndex,
        final int buttonNum,
        final @NonNull ContainerInput containerInput,
        final @NonNull Player player
    ) {
        System.out.println(
            "BLOCKED CLICK: " +
            slotIndex + " / " +
            containerInput
        );
        return;

        // Sub-Menu Navigations
        /*if( childrenMap.containsKey(slotIndex) ) {
            ImmutableChestMenu menu = childrenMap.get(slotIndex);
            ImmutableChestMenu.open(player, menu);
            //return;
        }

        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.containerMenu.broadcastFullState();
        }

        // Immutable, Chest slots (top)
        if (
            slotIndex >= 0 &&
            slotIndex < slots.size() &&
            slots.get(slotIndex).container == getContainer()
        ) {
            return;
        }

        // player inventory (bottom)
        super.clicked(
            slotIndex,
            buttonNum,
            containerInput,
            player
        );*/
    }

    @Override
    public @NonNull ItemStack quickMoveStack(
        @NonNull Player player,
        int slotId
    ) {
        return ItemStack.EMPTY;
    }


    /*boolean switchingMenu = false;
    @Override
    public void removed(final @NonNull Player player) {
        if( !(player instanceof ServerPlayer) ) {
            return;
        }

        // prevent recursion on child-close
        if(switchingMenu) {
            switchingMenu = false;
            return;
        }

        if(parent != null) {
            switchingMenu = true;
            System.out.println("exit");
            //TODO
            ( (ServerPlayer)player ).level().getServer().execute(
                () -> {
                    ImmutableChestMenu.open(player, parent);
                }
            );
        }
    }*/

}
