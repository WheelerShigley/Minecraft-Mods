package me.wheelershigley.www.solace_fishing.menus;

import me.wheelershigley.www.solace_fishing.registrations.FishItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static me.wheelershigley.www.solace_fishing.helpers.ItemsHelper.stackWithTranslatedName;

public class MainMenu extends ImmutableChestMenu {
    @Override
    public String getTranslationKey() {
        return "solace_fishing.main_menu.title";
    }

    @Override
    public MenuType<ChestMenu> getMenuType() {
        return MenuType.GENERIC_9x3;
    }

    private static final ItemStack
        sellItem = stackWithTranslatedName(Items.NAME_TAG, "solace_fishing.main_menu.sell_name"),
        buyItem  = stackWithTranslatedName(Items.EMERALD, "solace_fishing.main_menu.buy_name"),
        balItem  = stackWithTranslatedName(Items.PAPER, "solace_fishing.main_menu.balance_name"),

        skillsItem      = stackWithTranslatedName(Items.NETHER_STAR, "solace_fishing.main_menu.skills_name"),
        eventsItem      = stackWithTranslatedName(Items.PAPER, "solace_fishing.main_menu.event_progress_name"),
        leaderboardItem = stackWithTranslatedName(Items.WRITTEN_BOOK, "solace_fishing.main_menu.leaderboard_name")
    ;
    static {
        // The bundle and ominous-bottle is set indirectly here because actual items
        // have client-side lore that is undesirable for this context.
        balItem.set(
            DataComponents.ITEM_MODEL,
            Identifier.withDefaultNamespace("lime_bundle")
        );
        /* TODO: balance display
        balItem.set(
            DataComponents.LORE,
            ItemLore.EMPTY.withLineAdded(
                Components.literal(...)
            )
        );
         */

        eventsItem.set(
            DataComponents.ITEM_MODEL,
            Identifier.withDefaultNamespace("ominous_bottle")
        );
    }

    @Override
    public Container getContainer() {
        Container container = new SimpleContainer(9*3);

        // left-side: Economics
        container.setItem(0*9, sellItem);
        container.setItem(1*9, buyItem );
        container.setItem(2*9, balItem );

        // center: Index
        for(int colum = 0+2; colum <= 8-2; colum++) {
            for(int row = 0; row <= 2; row++) {
                int slot_index = colum + 9*row;
                ItemStack item = new ItemStack(FishItems.ANGELFISH);
                item.set(
                    DataComponents.CUSTOM_NAME,
                    Component.literal( Integer.toString(slot_index) )
                );
                container.setItem(slot_index, item);
            }
        }

        // right-side: Statistics
        container.setItem(1*9-1, skillsItem         );
        container.setItem(2*9-1, eventsItem         );
        container.setItem(3*9-1, leaderboardItem    );

        return container;
    }
}
