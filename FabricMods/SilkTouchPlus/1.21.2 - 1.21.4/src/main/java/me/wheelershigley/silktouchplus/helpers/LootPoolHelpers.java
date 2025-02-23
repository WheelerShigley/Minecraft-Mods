package me.wheelershigley.silktouchplus.helpers;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.predicate.item.ItemSubPredicateTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LootPoolHelpers extends FabricBlockLootTableProvider {
    protected LootPoolHelpers(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }
    @Override public void generate() {}

    private static LootCondition.Builder _createSilkTouchCondition(RegistryWrapper.WrapperLookup registries) {
        RegistryEntry<Enchantment> SilkTouchRegistryEntry = registries.createRegistryLookup().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
        return MatchToolLootCondition.builder(
            ItemPredicate.Builder.create().subPredicate(
                ItemSubPredicateTypes.ENCHANTMENTS, EnchantmentsPredicate.enchantments(
                    List.of(
                        new EnchantmentPredicate(
                            SilkTouchRegistryEntry,
                            NumberRange.IntRange.atLeast(1)
                        )
                    )
                )
            )
        );
    }

    private static final LootCondition.Builder pickaxesCondition =
        MatchToolLootCondition.builder(
            ItemPredicate.Builder.create().tag(
                TagKey.of(
                    RegistryKeys.ITEM,
                    Identifier.of("minecraft", "pickaxes")
                )
            )
        )
    ;
    public static void dropsWithSilkTouchPickaxe(LootTable.Builder tableBuilder, Block drop, RegistryWrapper.WrapperLookup registries) {
        LootCondition.Builder silkTouchCondition = _createSilkTouchCondition(registries);
        LootPool.Builder builder = LootPool.builder()
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop)
                    .conditionally(pickaxesCondition)
                    .conditionally(silkTouchCondition)
            )
        ;
        tableBuilder.pool( builder.build() );
    }
    public static void dropsSpawnerNBTWithSilkTouchPickaxe(LootTable.Builder tableBuilder, Block drop, RegistryWrapper.WrapperLookup registries) {
        LootCondition.Builder silkTouchCondition = _createSilkTouchCondition(registries);
        LootPool.Builder builder = LootPool.builder()
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop)
                    .conditionally(pickaxesCondition)
                    .conditionally(silkTouchCondition)
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
                    //.withOperation("Delay", "Delay")
                    .withOperation("MinSpawnDelay", "MinSpawnDelay")
                    .withOperation("SpawnPotentials", "SpawnPotentials")
                    .build()
            )
        ;
        tableBuilder.pool( builder.build() );
    }

    public static void dropsTrialSpawnerNBTWithSilkTouchPickaxe(LootTable.Builder tableBuilder, Block drop, RegistryWrapper.WrapperLookup registries) {
        LootCondition.Builder silkTouchCondition = _createSilkTouchCondition(registries);
        LootPool.Builder builder = LootPool.builder()
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop)
                    .conditionally(pickaxesCondition)
                    .conditionally(silkTouchCondition)
            )
            .apply(
                CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                    //TODO fix
                    /*  Components on a pick-block-ed trial-spawner BLOCK:
                        next_mob_spawns_at, current_mobs, total_mobs_spawned,
                        normal_config, spawn_data, id, ominous_config,

                        Components on a pick-block-ed trial-spawner ITEM:
                        next_mob_spawns_at, registered_players, total_mobs_spawned,
                        normal_config, spawn_data, id, ominous_config
                     */
                    .withOperation("next_mob_spawns_at", "next_mob_spawns_at")
                    //.withOperation("current_mobs", "current_mobs")
                    .withOperation("total_mobs_spawned", "total_mobs_spawned")
                    .withOperation("normal_config", "normal_config")
                    .withOperation("spawn_data", "spawn_data")
                    .withOperation("id", "id")
                    .withOperation("ominous_config", "ominous_config")
                    .build()
            )
        ;
        tableBuilder.pool( builder.build() );
    }

    private static final LootCondition.Builder shovelsCondition =
        MatchToolLootCondition.builder(
            ItemPredicate.Builder.create().tag(
                TagKey.of(
                    RegistryKeys.ITEM,
                    Identifier.of("minecraft", "shovels")
                )
            )
        )
    ;
    public static void dropsWithSilkTouchShovel(LootTable.Builder tableBuilder, Block drop, RegistryWrapper.WrapperLookup registries) {
        LootCondition.Builder silkTouchCondition = _createSilkTouchCondition(registries);
        LootPool.Builder builder = LootPool.builder()
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop)
                    .conditionally(shovelsCondition)
                    .conditionally(silkTouchCondition)
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
