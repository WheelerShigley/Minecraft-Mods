package me.wheelershigley.www.solace_crates.menus;

import me.wheelershigley.www.solace_crates.loot.ItemStackOddsTables;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class WrappedImmutableSimpleGui extends ImmutableSimpleGui {
    static final ItemStack FILLER;
    static final int ROW_LENGTH = 9;
    static final int BODY_LENGTH = 4*ROW_LENGTH;
    int PAGE_INDEX = 0;

    List<ItemStack> displayItems;

    public WrappedImmutableSimpleGui(
        @Nullable ServerPlayer player,
        @NotNull List<ItemStack> displayItems,
        @Nullable ImmutableSimpleGui parent
    ) {
        this.displayItems = displayItems;
        MenuType<?> menuType = getMinimumChestMenu(
        2*ROW_LENGTH + Math.clamp(
                displayItems.size(),
                0, BODY_LENGTH
            )
        );
        super(player, menuType, parent);
    }

    @Override
    public void initializeMenu() {
        fillBounds();
        setHeader();
        setBody();
        setFooter();
    }
    public void initializeMenu(final int pageNumber) {
        this.PAGE_INDEX = pageNumber;
        initializeMenu();
    }

    public abstract void setHeader();
    public abstract void setFooter();

    public void setBody() {
        final int STARTING_POSITION = PAGE_INDEX * BODY_LENGTH;
        final int ENDING_POSITION = Math.min(
            STARTING_POSITION + BODY_LENGTH,
            displayItems.size()
        );

        int position = 1*ROW_LENGTH;
        for(int index = STARTING_POSITION; index < ENDING_POSITION; index++) {
            setSlot( position++, displayItems.get(index) );
        }
    }

    int getPageCount() {
        final int ENTRY_COUNT = displayItems.size();
        final int BODY_LENGTH = 4*ROW_LENGTH;
        return (int)Math.ceil(
            (double)ENTRY_COUNT / (double)BODY_LENGTH
        );
    }
    int getTotalLength() {
        final int ENTRY_COUNT = displayItems.size();
        final int BODY_LENGTH = 4*ROW_LENGTH;
        return 2*ROW_LENGTH + Math.clamp(
            ENTRY_COUNT - ( (long)(PAGE_INDEX) ) * BODY_LENGTH,
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

    static {
        ItemStack filler = new ItemStack( Items.STAINED_GLASS_PANE.lightGray() );
        filler.set(
            DataComponents.CUSTOM_NAME,
            Component.literal(" ")
        );
        FILLER = filler;
    }
}
