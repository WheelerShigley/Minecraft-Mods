package external;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.resource.ResourceManager;

public class LordDeatHunter {
    //https://github.com/LordDeatHunter/SilkSpawners/blob/master/src/main/java/wraith/silkspawners/SilkSpawners.java
    public static void registerSilkTouchDrop(ResourceManager resourceManager, LootManager manager, LootTable.Builder supplier, Item blockItem, int minimum_level) {
        LootPool.Builder builder = LootPool.builder();
        builder.rolls(ConstantLootNumberProvider.create(1))
                .with(ItemEntry.builder(blockItem).build())
                .conditionally(
                        MatchToolLootCondition.builder(
                                ItemPredicate.Builder.create().enchantment(
                                        new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(minimum_level))
                                )
                        ).build()
                );
        supplier.pool(builder.build());
    }
    public static void registerSilkTouchDropWithNBT(ResourceManager resourceManager, LootManager manager, LootTable.Builder supplier, Item blockItem, int minimum_level) {
        LootPool.Builder builder = LootPool.builder();
        builder.rolls(ConstantLootNumberProvider.create(1))
                .with(ItemEntry.builder(blockItem).build())
                .apply(
                        CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                                .withOperation("SpawnData", "BlockEntityTag.SpawnData")
                                .withOperation("SpawnPotentials", "BlockEntityTag.SpawnPotentials")
                                .build()
                )
                .conditionally(
                        MatchToolLootCondition.builder(
                                ItemPredicate.Builder.create().enchantment(
                                        new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(minimum_level))
                                )
                        ).build()
                );
        supplier.pool(builder.build());
    }
}
