package me.wheelershigley.www.solace_crates.loot;

import me.wheelershigley.www.solace_crates.api.MathsHelper;
import me.wheelershigley.www.solace_crates.api.OddsTables;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ItemStackOddsTables extends OddsTables< ItemStackOddsTable, ItemStack> {
    public ItemStackOddsTables(ItemStackOddsTable table) {
        super( List.of(table) );
    }
    public ItemStackOddsTables(List<ItemStackOddsTable> tables) {
        super(tables);
    }

    @Override
    public List<ItemStack> getAllEntries() {
        List<ItemStack> stacks = new LinkedList<>();

        int pool_count = this.getOddsTables().size();
        int pool = 1;
        for(ItemStackOddsTable table : this.getOddsTables() ) {
            Map< List<ItemStack>, Double > weightMap = table.getSorted();
            double sum_weight = weightMap.values().stream().reduce(0.0, Double::sum);

            for( Map.Entry< List<ItemStack>, Double > entry : weightMap.entrySet() ) {
                if( entry.getKey().isEmpty() ) {
                    continue;
                }

                ItemStack currentItem = entry.getKey().getFirst().copy();
                //TODO: multi-item shuffling/lore
                weighStack(
                    currentItem,
                    (1 < pool_count ? pool : null),
                    entry.getValue() / sum_weight
                );

                //TODO: counts <------------

                stacks.add(currentItem);
            }

            pool++;
        }
        return stacks;
    }

    private static final Style LORE_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false);
    private void weighStack(
              @NotNull  ItemStack stack,
        final @Nullable Integer pool_number,
        final double percentage
    ) {
        Component line;
        String readablePercentage = MathsHelper.getHumanReadablePercentage(percentage);
        if( pool_number != null ) {
            line = Component.translatable(
                    "solace_crates.oddstables.itemstack.lore.pool",
                    pool_number,
                    readablePercentage
                ).withStyle(LORE_STYLE)
            ;
        } else {
            line = Component.literal(readablePercentage);
        }
        stack.set( DataComponents.LORE, ItemLore.EMPTY.withLineAdded(line) );
    }
}
