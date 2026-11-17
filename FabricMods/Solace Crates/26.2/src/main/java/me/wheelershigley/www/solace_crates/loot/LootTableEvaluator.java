package me.wheelershigley.www.solace_crates.loot;

import me.wheelershigley.www.solace_crates.mixin.*;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LootTableEvaluator {
    public static ItemStackOddsTables evaluate(
        @NotNull LootTable table,
        @NotNull LootContext context
    ) {
        List<LootPool> pools = ( (LootTableAccessor)table ).solace_crates$getPools();
        List< List<LootPoolEntry> > poolEntries = new LinkedList<>();
        for(LootPool pool : pools) {
            List<LootPoolEntry> currentEntries = new ArrayList<>();
            boolean conditionsRun = runs(pool.conditions, context);
            if(!conditionsRun) {
                continue;
            }

            boolean poolRuns = ( (LootPoolAccessor)pool ).solace_crates$getCompositeCondition().test(context);
            if(!poolRuns) {
                continue;
            }
            for(LootPoolEntryContainer entry : pool.entries) {
                entry.expand(context, currentEntries::add);
            }
            poolEntries.add(currentEntries);
        }

        List<ItemStackOddsTable> oddsTables = new LinkedList<>();
        for(List<LootPoolEntry> currentEntries : poolEntries) {
            oddsTables.add(
                new ItemStackOddsTable(currentEntries, context)
            );
        }
        return new ItemStackOddsTables(oddsTables);
    }

    private static boolean runs(
        @NotNull List<LootItemCondition> conditions,
        @NotNull LootContext context
    ) {
        for(LootItemCondition condition : conditions) {
            if( !condition.test(context) ) {
                return false;
            }
        }
        return true;
    }

    public static NumberProvider getProvider(LootPoolEntry entry) {
        LootPoolSingletonContainer container = ( (EntryBaseAccessor)entry ).solace_crates$getContainer();

        List<LootItemFunction> functions = (
            (LootPoolSingletonContainerAccessor)container
        ).solace_crates$getFunctions();

        for(LootItemFunction function : functions) {
            if( !(function instanceof SetItemCountFunction setItemCount) ) {
                continue;
            }

            return(
                (SetItemCountFunctionAccessor)setItemCount
            ).solace_crates$getCount();
        }

        return null;
    }
}
