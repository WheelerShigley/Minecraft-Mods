package me.wheelershigley.silktouchplus.helpers;

import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyComponentsLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

import static net.minecraft.data.server.loottable.BlockLootTableGenerator.WITH_SILK_TOUCH;

public class LootPoolHelpers {
    private static final LootCondition.Builder pickaxes_condition =
        MatchToolLootCondition.builder(
            ItemPredicate.Builder.create().tag(
                TagKey.of(
                    RegistryKeys.ITEM,
                    Identifier.of("minecraft", "pickaxes")
                )
            )
        )
    ;
    public static void dropsWithSilkTouchPickaxe(LootTable.Builder tableBuilder, Block drop) {
        LootPool.Builder builder = LootPool.builder()
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop)
                    .conditionally(pickaxes_condition)
                    .conditionally(WITH_SILK_TOUCH)
            )
        ;
        tableBuilder.pool( builder.build() );
    }
    public static void dropsSpawnerNBTWithSilkTouchPickaxe(LootTable.Builder tableBuilder, Block drop) {

        LootPool.Builder builder = LootPool.builder()
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop)
                    .conditionally(pickaxes_condition)
                    .conditionally(WITH_SILK_TOUCH)
            )
            .apply(
                CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                    /*  Components on a pick-block-ed spawner:
                        MaxNearbyEntities, RequiredPlayerRange, SpawnCount, SpawnData,
                        MaxSpawnDelay, id, SpawnRange, Delay, MinSpawnDelay, SpawnPotentials
                     */
                    .withOperation("MaxNearbyEntities", "MaxNearbyEntities")
                    .withOperation("RequiredPlayerRange", "RequiredPlayerRange")
                    .withOperation("SpawnCount", "SpawnCount")
                    .withOperation("SpawnData", "SpawnData")
                    .withOperation("MaxSpawnDelay", "MaxSpawnDelay")
                    .withOperation("id", "id")
                    .withOperation("SpawnRange", "SpawnRange")
                    .withOperation("Delay", "Delay")
                    .withOperation("MinSpawnDelay", "MinSpawnDelay")
                    .withOperation("SpawnPotentials", "SpawnPotentials")
                    .build()
            )
        ;
        tableBuilder.pool( builder.build() );
    }

    private static final LootCondition.Builder shovels_condition =
        MatchToolLootCondition.builder(
            ItemPredicate.Builder.create().tag(
                TagKey.of(
                    RegistryKeys.ITEM,
                    Identifier.of("minecraft", "shovels")
                )
            )
        )
    ;
    public static void dropsWithSilkTouchShovel(LootTable.Builder tableBuilder, Block drop) {
        LootPool.Builder builder = LootPool.builder()
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop)
                    .conditionally(shovels_condition)
                    .conditionally(WITH_SILK_TOUCH)
            )
            .apply(
                CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                /*  Components on a pick-block-ed suspicious gravel/sand:
                    LootTable, id, LootTableSeed
                 */
                .withOperation("LootTable", "LootTable")
                .withOperation("id", "id")
                .withOperation("LootTableSeed", "LootTableSeed")
                .build()
            )
        ;
        tableBuilder.pool( builder.build() );
    }
}
