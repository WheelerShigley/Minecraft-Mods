package me.wheelershigley.www.solace_crates.menus;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import me.wheelershigley.www.solace_crates.api.MathsHelper;
import me.wheelershigley.www.solace_crates.crate.CrateDefinition;
import me.wheelershigley.www.solace_crates.items.ItemsHelper;
import me.wheelershigley.www.solace_crates.loot.ItemStackOddsTables;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ProbabilitiesMenu extends WrappedImmutableSimpleGui {
    private int PAGE_INDEX = 0;

    public CrateDefinition crateDefinition;
    private final List<ItemStack> displayItems;

    public ProbabilitiesMenu(
        @Nullable ServerPlayer player,
        @NotNull ItemStackOddsTables oddsTables,
        @Nullable CrateDefinition crateDefinition,
        @Nullable ImmutableSimpleGui parent
    ) {
        this.crateDefinition = crateDefinition;

        List<ItemStack> displayItems = sanitizeItems( oddsTables.getAllEntries() );
        super(player, displayItems, parent);
        this.displayItems = displayItems;

        initializeMenu(PAGE_INDEX);
    }

    @Override
    public void setHeader() {
        CrateMetaItems menuItemsMaker = new CrateMetaItems(crateDefinition);

        /* Key(s) */ {
            int key_position = 0;

            ItemStack standardKeyMenuItem = menuItemsMaker.getStandardKeyMenuItem();
            if(standardKeyMenuItem != ItemStack.EMPTY) {
                setSlot( key_position++, menuItemsMaker.getStandardKeyMenuItem() );
            }

            ItemStack premiumKeyMenuItem = menuItemsMaker.getPremiumKeyMenuItem();
            if(premiumKeyMenuItem != ItemStack.EMPTY) {
                setSlot( key_position++, menuItemsMaker.getPremiumKeyMenuItem() );
            }
        }

        // TODO -(s)
        setSlot( 4, menuItemsMaker.getNextItem()            );
        setSlot( 7, menuItemsMaker.getPityItem()            );
        setSlot( 8, menuItemsMaker.getStoreLinkItem()       );
    }

    @Override
    public void setFooter() {
        CrateMetaItems menuItemsMaker = new CrateMetaItems(crateDefinition);

        final int PAGE_COUNT = getPageCount();
        final int FOOTER_POSITION; {
            if(PAGE_INDEX != 0) {
                FOOTER_POSITION = 5*ROW_LENGTH;
            } else {
                FOOTER_POSITION = getTotalLength() - ROW_LENGTH;
            }
        }

        if(0 < PAGE_INDEX && PAGE_INDEX <= PAGE_COUNT-1) {
            GuiElement backArrow = GuiElementBuilder
                .from(
                    menuItemsMaker.getBackArrowItem()
                ).setCallback(
                    (_, _, _, _) -> {
                        PAGE_INDEX--;
                        refresh();
                    }
                )
                .build()
            ;
            setSlot(FOOTER_POSITION + 0, backArrow);
        } else {
            setSlot(FOOTER_POSITION + 0, FILLER);
        }

        if(0 <= PAGE_INDEX && PAGE_INDEX < PAGE_COUNT-1) {
            GuiElement forwardArrow = GuiElementBuilder
                .from(
                    menuItemsMaker.getForwardArrowItem()
                ).setCallback(
                    (_, _, _, _) -> {
                        PAGE_INDEX++;
                        refresh();
                    }
                )
                .build()
            ;
            setSlot(FOOTER_POSITION + 8, forwardArrow);
        } else {
            setSlot(FOOTER_POSITION + 8, FILLER);
        }
    }

    @Override
    public String getTranslationKey() {
        return "solace_crates.crate.title";
    }

    @Override
    public MenuType<?> getMenuType() {
        return getMinimumChestMenu(
            2*ROW_LENGTH + this.displayItems.size()
        );
    }

    public void refresh() {
        clearBody();
        initializeMenu(this.PAGE_INDEX);
    }

    private void clearBody() {
        for(int index = ROW_LENGTH; index < getTotalLength() - ROW_LENGTH; index++) {
            setSlot(index, ItemStack.EMPTY);
        }
    }

    private static List<ItemStack> sanitizeItems(List<ItemStack> items) {
        List<ItemStack> sanitizedItems = new LinkedList<>();
        for(ItemStack item : items) {
            ItemStack currentStack = new ItemStack(Items.PAPER);
            currentStack.set(
                DataComponents.ITEM_MODEL,
                ItemsHelper.getIdentifier( item.getItem() )
            );

            DataComponentMap components = item.getComponents();
            for (TypedDataComponent<?> component : components) {
                if (component.type() == DataComponents.ITEM_MODEL) {
                    continue;
                }

                setComponent(currentStack, component);
            }

            sanitizedItems.add(currentStack);
        }

        return sanitizedItems;
    }

    private class CrateMetaItems {
        CrateDefinition crateDefinition;

        public CrateMetaItems(CrateDefinition definition) {
            this.crateDefinition = definition;
        }

        private ItemStack getMetaMenuItem(
            Identifier itemIdentifier,
            String translationKey,
            Style nameStyle, Style loreStyle
        ) {
            if(this.crateDefinition == null) {
                return ItemStack.EMPTY;
            }

            DataComponentMap name_map;
            Item item = ItemsHelper.getItem(itemIdentifier);
            if(item != null) {
                name_map = DataComponentMap.builder()
                    .set(
                        DataComponents.CUSTOM_NAME,
                        Component.translatable( item.getDescriptionId() ).withStyle(nameStyle)
                    )
                    .set(
                        DataComponents.LORE,
                        ItemLore.EMPTY.withLineAdded(
                            Component
                                .translatable( item.getDescriptionId() ).withStyle(loreStyle)
                        )
                    )
                    .build()
                ;
            } else {
                return WrappedImmutableSimpleGui.FILLER;
            }

            return getMenuItem(item, name_map, false, translationKey);
        }

        public ItemStack getStandardKeyMenuItem() {
            if(crateDefinition == null) {
                return ItemStack.EMPTY;
            }

            return getMetaMenuItem(
                crateDefinition.STANDARD_KEY,
                "solace_crates.menu.probabilities.standard_key",
                Style.EMPTY.withColor(TextColor.GRAY).withItalic(true),
                Style.EMPTY.withColor(TextColor.YELLOW).withBold(true)
            );
        }
        public ItemStack getPremiumKeyMenuItem() {
            if(crateDefinition == null) {
                return ItemStack.EMPTY;
            }

            return getMetaMenuItem(
                crateDefinition.PREMIUM_KEY,
                "solace_crates.menu.probabilities.premium_key",
                Style.EMPTY.withColor(TextColor.GRAY).withItalic(true),
                Style.EMPTY.withColor(TextColor.GOLD).withBold(true)
            );
        }
        // TODO
        public ItemStack getNextItem() {
            ServerPlayer player = getPlayer();
            int seed = 0;

            return getMenuItem(
                Items.PAPER,
                false,
                "solace_crates.menu.probabilities.next_item"
            );
        }
        // TODO
        public ItemStack getPityItem() {
            return getMenuItem(
                Items.SUNFLOWER,
                false,
                "solace_crates.menu.probabilities.pity"
            );
        }
        // TODO
        public ItemStack getStoreLinkItem() {
            return getMenuItem(
                Items.WRITTEN_BOOK,
                false,
                "solace_crates.menu.probabilities.link." + "todo"
            );
        }

        public ItemStack getBackArrowItem() {
            return getMenuItem(
                Items.ARROW,
                false,
                "solace_crates.menu.probabilities.previous_page"
            );
        }
        public ItemStack getForwardArrowItem() {
            return getMenuItem(
                Items.ARROW,
                false,
                "solace_crates.menu.probabilities.next_page"
            );
        }
    }
}
