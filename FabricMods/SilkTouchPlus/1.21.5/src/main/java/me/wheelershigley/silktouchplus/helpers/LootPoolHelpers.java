package me.wheelershigley.silktouchplus.helpers;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
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
import net.minecraft.predicate.component.ComponentPredicateTypes;
import net.minecraft.predicate.item.*;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

//I used VanillaLootTableGenerator.class as a reference for the static members of this class
public class LootPoolHelpers extends FabricBlockLootTableProvider {
    protected LootPoolHelpers(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }
    @Override public void generate() {}

    //BlockLootTableGenerator.createSilkTouchCondition()
    public static LootCondition.Builder createSilkTouchCondition(RegistryWrapper.WrapperLookup registries) {
        return MatchToolLootCondition.builder(
            ItemPredicate.Builder.create().components(
                net.minecraft.predicate.component.ComponentsPredicate.Builder.create().partial(
                    ComponentPredicateTypes.ENCHANTMENTS,
                    EnchantmentsPredicate.enchantments(
                        List.of(
                            new EnchantmentPredicate(
                                registries.getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH),
                                NumberRange.IntRange.atLeast(1)
                            )
                        )
                    )
                ).build()
            )
        );
    }

    private static LootCondition.Builder pickaxesCondition(RegistryWrapper.WrapperLookup registries) {
        return MatchToolLootCondition.builder(
            ItemPredicate.Builder.create().tag(
                registries.getOrThrow(RegistryKeys.ITEM),
                ItemTags.PICKAXES
            )
        );
    }

    public static void dropsWithSilkTouchPickaxe(LootTable.Builder tableBuilder, Block drop, RegistryWrapper.WrapperLookup registries) {
        LootCondition.Builder silkTouchCondition = createSilkTouchCondition(registries);
        LootPool.Builder builder = LootPool.builder()
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop)
                    .conditionally( pickaxesCondition(registries) )
                    .conditionally(silkTouchCondition)
            )
        ;
        tableBuilder.pool( builder.build() );
    }
    public static void dropsSpawnerNBTWithSilkTouchPickaxe(LootTable.Builder tableBuilder, Block drop, RegistryWrapper.WrapperLookup registries) {
        LootCondition.Builder silkTouchCondition = createSilkTouchCondition(registries);
        LootPool.Builder builder = LootPool.builder()
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop)
                    .conditionally( pickaxesCondition(registries) )
                    .conditionally(silkTouchCondition)
            )
            .apply(
                CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                    /*
                    Block:
                        MaxNearbyEntities, RequiredPlayerRange, SpawnCount, SpawnData,
                        MaxSpawnDelay, id, SpawnRange, MinSpawnDelay, SpawnPotentials
                    Item:
                        MaxNearbyEntities, RequiredPlayerRange, SpawnCount, SpawnData,
                        MaxSpawnDelay, id, SpawnRange, MinSpawnDelay, SpawnPotentials
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
        LootCondition.Builder silkTouchCondition = createSilkTouchCondition(registries);
        LootPool.Builder builder = LootPool.builder()
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop)
                    .conditionally( pickaxesCondition(registries) )
                    .conditionally(silkTouchCondition)
            )
            .apply(
                CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                    /*  Components on a pick-block-ed trial-spawner BLOCK:
                        next_mob_spawns_at, current_mobs, registered_players,
                        total_mobs_spawned, normal_config, spawn_data, id, ominous_config

                        Components on a pick-block-ed trial-spawner ITEM:
                        normal_config, spawn_data, id, ominous_config
                     */
                    .withOperation("normal_config", "normal_config")
                    .withOperation("spawn_data", "spawn_data")
                    .withOperation("id", "id")
                    .withOperation("ominous_config", "ominous_config")
                    .build()
            )
        ;
        tableBuilder.pool( builder.build() );
    }

    public static void dropVaultNBTWithSilkTouchPickaxe(LootTable.Builder tableBuilder, Block drop, RegistryWrapper.WrapperLookup registries) {
        LootCondition.Builder silkTouchCondition = createSilkTouchCondition(registries);
        LootPool.Builder builder = LootPool.builder()
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop)
                    .conditionally( pickaxesCondition(registries) )
                    .conditionally(silkTouchCondition)
            )
            .apply(
                CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                    /*  Components on a pick-block-ed vault:
                        server_data, id, config, shared_data
                     */
                    .withOperation("server_data", "server_data")
                    .withOperation("id", "id")
                    .withOperation("config", "config")
                    .withOperation("shared_data", "shared_data")
                    .build()
            )
        ;
        tableBuilder.pool( builder.build() );
    }

    private static LootCondition.Builder shovelsCondition(RegistryWrapper.WrapperLookup registries) {
        return MatchToolLootCondition.builder(
            ItemPredicate.Builder.create().tag(
                registries.getOrThrow(RegistryKeys.ITEM),
                ItemTags.SHOVELS
            )
        );
    }
    public static void dropsWithSilkTouchShovel(LootTable.Builder tableBuilder, Block drop, RegistryWrapper.WrapperLookup registries) {
        LootCondition.Builder silkTouchCondition = createSilkTouchCondition(registries);
        LootPool.Builder builder = LootPool.builder()
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop)
                    .conditionally( shovelsCondition(registries) )
                    .conditionally(silkTouchCondition)
            )
            .apply(
                //TODO Fix that this uses "custom_data" from the Provider below
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
