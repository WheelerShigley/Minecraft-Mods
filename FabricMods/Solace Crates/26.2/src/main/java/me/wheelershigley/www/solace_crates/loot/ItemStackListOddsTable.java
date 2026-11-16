package me.wheelershigley.www.solace_crates.loot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ItemStackListOddsTable implements OddsTable< List<ItemStack> > {
    private final HashMap< List<ItemStack>, Double > DATA = new HashMap<>();

    public ItemStackListOddsTable(
        @NotNull List<LootPoolEntry> entries,
        @NotNull LootContext context
    ) {
        for(LootPoolEntry entry : entries) {
            List<ItemStack> itemStacks = new ArrayList<>();
            entry.createItemStack(itemStacks::add, context);

            addOdd(
                itemStacks,
                entry.getWeight( context.getLuck() )
            );
        }

        normalize();
        sort();
    }

    @Override
    public HashMap< List<ItemStack>, Double > getData() {
        return DATA;
    }

    @Override
    public Map< List<ItemStack>, Double> getSorted() {
        return getData().entrySet().stream()
            .sorted(
                Map.Entry.<List<ItemStack>, Double>comparingByValue()
                    .reversed()
                    .thenComparing(
                        entry -> entry.getKey().getFirst().getItem().getDescriptionId()
                    )
            )
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (a, b) -> a, LinkedHashMap::new
                )
            )
        ;
    }
}
