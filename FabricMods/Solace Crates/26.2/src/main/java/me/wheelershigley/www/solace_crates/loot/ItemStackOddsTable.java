package me.wheelershigley.www.solace_crates.loot;

import me.wheelershigley.www.solace_crates.api.EntryOutcome;
import me.wheelershigley.www.solace_crates.api.OddsTable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ItemStackOddsTable extends OddsTable<EntryOutcome> {
    private final HashMap<EntryOutcome, Double> DATA = new HashMap<>();

    public ItemStackOddsTable(
        @NotNull List<LootPoolEntry> entries,
        @NotNull LootContext context
    ) {
        for(LootPoolEntry entry : entries) {

            NumberProvider entryProvider = LootTableEvaluator.getProvider(entry);
            System.out.println( "entry-rolls: "+ entryProvider );

            List<ItemStack> stacks = new LinkedList<>();
            entry.createItemStack(stacks::add, context);

            addOdd(
                new EntryOutcome(entry, stacks),
                entry.getWeight( context.getLuck() )
            );
        }

        normalize();
        sort();
    }

    @Override
    public HashMap<EntryOutcome, Double> getData() {
        return DATA;
    }

    @Override
    public Map<EntryOutcome, Double> getSorted() {
        return getData().entrySet().stream()
            .sorted(
                Map.Entry.<EntryOutcome, Double>comparingByValue()
                    .reversed()
                    .thenComparing(
                        entry -> entry.getKey().getStacks().getFirst().getItem().getDescriptionId()
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
