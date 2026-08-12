package me.wheelershigley.www.solace_fishing.menus;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.gui.SlotBasedGui;
import me.wheelershigley.www.solace_fishing.registrations.FishItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static me.wheelershigley.www.solace_fishing.helpers.ItemsHelper.stackWithTranslatedName;

public class MainMenu extends ImmutableSimpleGui {
    private static final MenuType<?> MENU_TYPE = MenuType.GENERIC_9x3;

    public MainMenu(ServerPlayer player) {
        super(player, MainMenu.MENU_TYPE, null);
    }

    @Override
    public String getTranslationKey() {
        return "solace_fishing.main_menu.title";
    }

    @Override
    public MenuType<?> getMenuType() {
        return MainMenu.MENU_TYPE;
    }

    @Override
    public void initializeMenu() {
        final ItemStack
            sellItem = stackWithTranslatedName(Items.NAME_TAG, "solace_fishing.main_menu.sell_name"),
            buyItem  = stackWithTranslatedName(Items.EMERALD, "solace_fishing.main_menu.buy_name"),
            balItem  = stackWithTranslatedName(Items.PAPER, "solace_fishing.main_menu.balance_name"),

            skillsItem      = stackWithTranslatedName(Items.NETHER_STAR, "solace_fishing.main_menu.skills_name"),
            eventsItem      = stackWithTranslatedName(Items.PAPER, "solace_fishing.main_menu.event_progress_name"),
            leaderboardItem = stackWithTranslatedName(Items.WRITTEN_BOOK, "solace_fishing.main_menu.leaderboard_name")
        ;
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

        GuiElement.ClickCallback sellNavigator = (index, type, action, gui) -> {
            ( new SellMenu(player, this) ).open();
        };

        // left-side: Economics
        this.setSlot(0*ROW_LENGTH, sellItem, sellNavigator);
        this.setSlot(1*ROW_LENGTH, buyItem );
        this.setSlot(2*ROW_LENGTH, balItem );

        // center: Index
        for(int colum = 0+2; colum <= ROW_LENGTH-3; colum++) {
            for(int row = 0; row <= 2; row++) {
                int slot_index = colum + ROW_LENGTH*row;
                ItemStack item = new ItemStack(FishItems.ANGELFISH);
                item.set(
                    DataComponents.CUSTOM_NAME,
                    Component.literal( Integer.toString(slot_index) )
                );
                this.setSlot(slot_index, item);
            }
        }

        // right-side: Statistics
        this.setSlot(1*ROW_LENGTH-1, skillsItem         );
        this.setSlot(2*ROW_LENGTH-1, eventsItem         );
        this.setSlot(3*ROW_LENGTH-1, leaderboardItem    );
    }
}
