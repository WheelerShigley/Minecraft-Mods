package me.wheelershigley.www.solace_crates.loot;

import me.wheelershigley.www.solace_crates.mixin.LootPoolAccessor;
import me.wheelershigley.www.solace_crates.mixin.LootTableAccessor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LootTableEvaluator {
    public static ItemStackListOddsTable evaluate(
        @NotNull LootTable table,
        @NotNull LootContext context
    ) {
        List<LootPool> pools = ( (LootTableAccessor)table ).solace_crates$getPools();

        List<LootPoolEntry> entries = new ArrayList<>();
        for(LootPool pool : pools) {
            // TODO: use rolls (?)
            // int rolls = pool.rolls.getInt(context) + pool.bonusRolls.getInt(context);

            boolean conditionsRun = runs(pool.conditions, context);
            if(!conditionsRun) {
                continue;
            }

            boolean poolRuns = ( (LootPoolAccessor)pool ).solace_crates$getCompositeCondition().test(context);
            if(!poolRuns) {
                continue;
            }
            for(LootPoolEntryContainer entry : pool.entries) {
                entry.expand(context, entries::add);
            }
        }

        return new ItemStackListOddsTable(entries, context);
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
}
