package me.wheelershigley.www.solace_fishing.menus;

import eu.pb4.sgui.api.elements.GuiElement;
import me.wheelershigley.www.solace_fishing.registrations.FishItems;
import me.wheelershigley.www.solace_fishing.registrations.FishingItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static me.wheelershigley.www.solace_fishing.helpers.ItemsHelper.getMenuItem;

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
            sellItem = getMenuItem(Items.NAME_TAG,                 false, "solace_fishing.main_menu.sell_name"           ),
            buyItem  = getMenuItem(FishingItems.PDA,               false, "solace_fishing.main_menu.buy_name"            ),
            balItem  = getMenuItem(Items.DYED_BUNDLE.lime(),       false, "solace_fishing.main_menu.balance_name"        ),
            upgradesItem = getMenuItem(Items.NETHER_STAR,          false, "solace_fishing.main_menu.upgrades_name"          ),
            eventsItem      = getMenuItem(Items.OMINOUS_TRIAL_KEY, false, "solace_fishing.main_menu.event_progress_name" ),
            leaderboardItem = getMenuItem(Items.WRITTEN_BOOK,      false, "solace_fishing.main_menu.leaderboard_name"    )
        ;
        GuiElement.ClickCallback
            sellNavigator = (index, type, action, gui) -> {
                ( new SellMenu(player, this) ).open();
            },
            buyNavigator = (index, type, action, gui) -> {
                ( new BuyMenu(player, this) ).open();
            },
            upgradeNavigator = (index, type, action, gui) -> {
                ( new UpgradesMenu(player, this) ).open();
            },
            leaderboardNavigator = (index, type, action, gui) -> {
                ( new LeaderboardsMenu(player, this) ).open();
            }
        ;

        // left-side: Economics
        this.setSlot(0*ROW_LENGTH, sellItem, sellNavigator  );
        this.setSlot(1*ROW_LENGTH, buyItem,  buyNavigator   );
        this.setSlot(2*ROW_LENGTH, balItem                  );

        // center: Index
        for(int colum = 0+2; colum <= ROW_LENGTH-3; colum++) {
            for(int row = 0; row <= 2; row++) {
                int slot_index = colum + ROW_LENGTH*row;
                ItemStack item = getMenuItem(FishItems.ANGELFISH, false, null);
                item.set(
                    DataComponents.CUSTOM_NAME,
                    Component.literal( Integer.toString(slot_index) )
                );
                this.setSlot(slot_index, item);
            }
        }

        // right-side: Statistics
        this.setSlot(1*ROW_LENGTH-1, upgradesItem,    upgradeNavigator      );
        this.setSlot(2*ROW_LENGTH-1, eventsItem                             );
        this.setSlot(3*ROW_LENGTH-1, leaderboardItem, leaderboardNavigator  );
    }
}
