package me.wheelershigley.www.solace_fishing.menus;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Deprecated
public class SellMenu extends ImmutableChestMenu {
    private final ServerPlayer player;
    public static final MenuType<ChestMenu> MENU_TYPE = MenuType.GENERIC_9x6;
    public static final Container container; static {
        container = new SimpleContainer(6*9);

        container.setItem( 0, new ItemStack(Items.STICK) );
    }

    public SellMenu(ServerPlayer player, ImmutableChestMenu parent) {
        player.nextContainerCounter();

        super(
            MenuType.GENERIC_9x6,
            player.containerCounter,
            player.getInventory(),
            container,
            container.getContainerSize()/9
        );
        setParent(parent);

        this.player = player;
    }

    @Override
    public void addChildren() {
        addChild(
            0,
            new MainMenu(this.player)
            //this.getParent()
        );
    }

    @Override
    public MenuType<ChestMenu> getMenuType() {
        return SellMenu.MENU_TYPE;
    }
    @Override
    public Container getContainer() {
        return SellMenu.container;
    }

    @Override
    public String getTranslationKey() {
        return "solace_fishing.sell_menu.title";
    }
}
