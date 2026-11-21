package me.wheelershigley.www.solace_crates.menus;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import me.wheelershigley.www.solace_crates.api.EntryOutcome;
import me.wheelershigley.www.solace_crates.items.ItemsHelper;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public abstract class WrappedImmutableSimpleGui extends ImmutableSimpleGui {
    static final ItemStack FILLER;
    static final int ROW_LENGTH = 9;
    static final int BODY_LENGTH = 4*ROW_LENGTH;

    List<EntryOutcome> displayOutcomes;
    int page_index = 0;

    public WrappedImmutableSimpleGui(
        @Nullable ServerPlayer player,
        @NotNull List<EntryOutcome> displayOutcomes,
        @Nullable LootContext context,
        @Nullable ImmutableSimpleGui parent
    ) {
        if(context == null) {
            this.displayOutcomes = displayOutcomes;
        } else {
            this.displayOutcomes = sanitizeItems( displayOutcomes, context );
        }

        MenuType<?> menuType = getMinimumChestMenu(
        2*ROW_LENGTH + Math.clamp(
                displayOutcomes.size(),
                0, BODY_LENGTH
            )
        );
        super(player, menuType, parent);
    }

    @Override
    public void initializeMenu() {
        fillBounds();
        setHeader();
        tickBody();
        setFooter();
    }
    public void initializeMenu(final int pageNumber) {
        this.page_index = pageNumber;
        initializeMenu();
    }

    public void refresh() {
        clearBody();
        initializeMenu(this.page_index);
    }
    private void clearBody() {
        for(int index = ROW_LENGTH; index < getTotalLength(0) - ROW_LENGTH; index++) {
            setSlot(index, ItemStack.EMPTY);
        }
    }

    public abstract void setHeader();
    public void setFooter() {
        MetaItems menuItemsMaker = new MetaItems();

        final int PAGE_COUNT = getPageCount();
        final int FOOTER_POSITION; {
            if(page_index != 0) {
                FOOTER_POSITION = 5*ROW_LENGTH;
            } else {
                FOOTER_POSITION = getTotalLength() - ROW_LENGTH;
            }
        }

        if(0 < page_index && page_index <= PAGE_COUNT-1) {
            GuiElement backArrow = GuiElementBuilder
                .from(
                    menuItemsMaker.getBackArrowItem()
                ).setCallback(
                    (_, _, _, _) -> {
                        page_index--;
                        refresh();
                    }
                )
                .build()
            ;
            setSlot(FOOTER_POSITION + 0, backArrow);
        } else {
            setSlot(FOOTER_POSITION + 0, FILLER);
        }

        if(0 <= page_index && page_index < PAGE_COUNT-1) {
            GuiElement forwardArrow = GuiElementBuilder
                .from(
                    menuItemsMaker.getForwardArrowItem()
                ).setCallback(
                    (_, _, _, _) -> {
                        page_index++;
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

    public abstract void onBodyClick(final int index);

    public void tickBody() {
        final int STARTING_POSITION = page_index * BODY_LENGTH;
        final int ENDING_POSITION = Math.min(
            STARTING_POSITION + BODY_LENGTH,
            displayOutcomes.size()
        );

        int position = 1*ROW_LENGTH;
        for(int index = STARTING_POSITION; index < ENDING_POSITION; index++) {
            int true_index = index;
            SimpleGuiElement guiElement = GuiElementBuilder
                .from( displayOutcomes.get(index).tick() )
                .setCallback(
                    (_, _, _, _) -> {
                        onBodyClick(true_index);
                    }
                )
                .build()
            ;
            setSlot(position++, guiElement);
        }
    }

    int getPageCount() {
        final int ENTRY_COUNT = displayOutcomes.size();
        final int BODY_LENGTH = 4*ROW_LENGTH;
        return (int)Math.ceil(
            (double)ENTRY_COUNT / (double)BODY_LENGTH
        );
    }
    int getTotalLength() {
        return getTotalLength(page_index);
    }
    int getTotalLength(int page_index) {
        final int ENTRY_COUNT = displayOutcomes.size();
        final int BODY_LENGTH = 4*ROW_LENGTH;
        return 2*ROW_LENGTH + Math.clamp(
            ENTRY_COUNT - ( (long)(page_index) ) * BODY_LENGTH,
            0, BODY_LENGTH
        );
    }

    private void fillBounds() {
        // Header
        for(int index = 0; index < ROW_LENGTH; index++) {
            this.setSlot(index, FILLER);
        }

        // Footer
        final int FOOTER_POSITION = getTotalLength() - ROW_LENGTH;
        for(int index = FOOTER_POSITION; index < FOOTER_POSITION + ROW_LENGTH; index++) {
            this.setSlot(index, FILLER);
        }
    }

    static List<EntryOutcome> sanitizeItems(
        @NotNull List<EntryOutcome> entries,
        @NotNull LootContext lootContext
    ) {
        List<EntryOutcome> sanitizedEntries = new LinkedList<>();
        for(EntryOutcome entry : entries) {
            EntryOutcome.IntRange range = entry.getRange(lootContext);

            List<ItemStack> entryStacks = entry.getStacks();
            List<ItemStack> sanitizedStacks = new LinkedList<>();
            for(ItemStack stack : entryStacks) {
                sanitizedStacks.add( sanitizeItem(stack, range) );
            }

            EntryOutcome sanitizedEntry = new EntryOutcome(
                entry.getEntry(),
                entry.getNumberProvider(),
                sanitizedStacks
            );
            sanitizedEntries.add(sanitizedEntry);
        }

        return sanitizedEntries;
    }
    static ItemStack sanitizeItem(
        @NotNull final ItemStack datumStack,
        @Nullable final EntryOutcome.IntRange range
    ) {
        ItemStack sanitizedStack = new ItemStack(Items.PAPER);
        if( EntryOutcome.IntRange.isSimple(range, datumStack) ) {
            sanitizedStack.setCount( datumStack.getCount() );
        }

        sanitizedStack.set(
            DataComponents.ITEM_MODEL,
            ItemsHelper.getIdentifier( datumStack.getItem() )
        );

        DataComponentMap components = datumStack.getComponents();
        for (TypedDataComponent<?> component : components) {
            if (component.type() == DataComponents.ITEM_MODEL) {
                continue;
            }

            setComponent(sanitizedStack, component);
        }

        return sanitizedStack;
    }

    static {
        ItemStack filler = new ItemStack( Items.STAINED_GLASS_PANE.lightGray() );
        filler.set(
            DataComponents.CUSTOM_NAME,
            Component.literal(" ")
        );
        FILLER = filler;
    }

    private class MetaItems {
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
