package me.wheelershigley.www.solace_crates.loot;

import me.wheelershigley.www.solace_crates.api.EntryOutcome;
import me.wheelershigley.www.solace_crates.api.MathsHelper;
import me.wheelershigley.www.solace_crates.api.OddsTables;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ItemStackOddsTables extends OddsTables<ItemStackOddsTable, EntryOutcome> {
    public ItemStackOddsTables(ItemStackOddsTable table) {
        super( List.of(table) );
    }
    public ItemStackOddsTables(List<ItemStackOddsTable> tables) {
        super(tables);
    }

    private static final Style COUNT_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false);
    @Override
    public List<EntryOutcome> getAllEntries(LootContext context) {
        List<EntryOutcome> entries = new LinkedList<>();

        int pool_count = this.getOddsTables().size();
        int pool = 1;
        for(ItemStackOddsTable table : this.getOddsTables() ) {
            Map<EntryOutcome, Double> weightMap = table.getSorted();
            double sum_weight = weightMap.values().stream().reduce(0.0, Double::sum);

            for( Map.Entry<EntryOutcome, Double> entry : weightMap.entrySet() ) {
                EntryOutcome key = entry.getKey();
                List<ItemStack> entryItems = key.getStacks();
                if( entryItems.isEmpty() ) {
                    continue;
                }

                List<ItemStack> taggedItems = new LinkedList<>();
                for(ItemStack entryItem : entryItems) {
                    ItemStack currentItem = entryItem.copy();

                    EntryOutcome.IntRange range = key.getRange(context);
                    if( !EntryOutcome.IntRange.isSimple(range, currentItem) ) {
                        currentItem.set(
                            DataComponents.LORE,
                            ItemLore.EMPTY.withLineAdded( range.toComponent().copy().withStyle(COUNT_STYLE) )
                        );
                    }

                    weighStack(
                        currentItem,
                        (1 < pool_count ? pool : null),
                        entry.getValue() / sum_weight
                    );

                    taggedItems.add(currentItem);
                }

                entries.add(
                    new EntryOutcome(
                        key.getEntry(),
                        key.getNumberProvider(),
                        taggedItems
                    )
                );
            }

            pool++;
        }
        return entries;
    }

    private static final Style LORE_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false);
    private void weighStack(
              @NotNull  ItemStack stack,
        final @Nullable Integer pool_number,
        final double percentage
    ) {
        ItemLore existingLore = stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        List<Component> lines = new LinkedList<>( existingLore.lines() );

        String readablePercentage = MathsHelper.getHumanReadablePercentage(percentage);
        if( pool_number != null ) {
            lines.add(
                Component.translatable(
                    "solace_crates.oddstables.itemstack.lore.pool",
                    pool_number,
                    readablePercentage
                ).withStyle(LORE_STYLE)
            );
        } else {
            lines.add(
                Component.literal(readablePercentage)
            );
        }
        stack.set( DataComponents.LORE, new ItemLore(lines) );
    }
}
