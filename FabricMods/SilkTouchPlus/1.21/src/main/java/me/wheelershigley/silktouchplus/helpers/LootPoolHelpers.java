package me.wheelershigley.silktouchplus.helpers;

import net.minecraft.block.Block;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import static net.minecraft.data.server.loottable.BlockLootTableGenerator.WITH_SILK_TOUCH;

public class LootPoolHelpers {
    private static LootCondition.Builder pickaxes_condition =
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
                CopyNbtLootFunction.builder(LootContext.EntityTarget.THIS)
                    .withOperation("SpawnData", "BlockEntityData.SpawnData")
                    .withOperation("SpawnPotentials", "BlockEntityData.SpawnPotentials")
                    .build()
            )
            /*.apply(
                CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                    .withOperation("SpawnData", "BlockEntityTag.SpawnData")
                    .withOperation("SpawnPotentials", "BlockEntityTag.SpawnPotentials")
                    .build()
            )*/
        ;
        tableBuilder.pool( builder.build() );
    }

    private static LootCondition.Builder shovels_condition =
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
        ;
        tableBuilder.pool( builder.build() );
    }
}
