package me.wheelershigley.www.solace_fishing.menus;

import me.wheelershigley.www.solace_fishing.registrations.FishItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MainMenu extends ImmutableChestMenu {
    @Override
    public String getTranslationKey() {
        return "solace_fishing.main_menu.title";
    }

    @Override
    public MenuType<ChestMenu> getMenuType() {
        return MenuType.GENERIC_9x3;
    }

    @Override
    public Container getContainer() {
        Container container = new SimpleContainer(9*3);

        container.setItem(0, new ItemStack(Items.COMPASS)       );
        container.setItem(4, new ItemStack(Items.WRITTEN_BOOK)  );
        container.setItem(8, new ItemStack(Items.COMPASS)       );

        for(int a = 9; a < 2*9; a++) {
            ItemStack item = new ItemStack(FishItems.ANGELFISH);
            item.set(
                DataComponents.CUSTOM_NAME,
                Component.literal( Integer.toString(a-8) )
            );
            container.setItem(a, item);
        }

        container.setItem(2*9, new ItemStack(Items.TEST_BLOCK) );
        container.setItem(3*9-1, new ItemStack(Items.TEST_BLOCK) );

        return container;
    }
}
