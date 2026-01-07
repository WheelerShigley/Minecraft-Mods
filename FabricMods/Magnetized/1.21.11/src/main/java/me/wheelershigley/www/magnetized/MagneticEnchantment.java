package me.wheelershigley.www.magnetized;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

public class MagneticEnchantment {
    public static void register() {
        LootTableEvents.MODIFY.register(
            MagneticEnchantment::modifyLootTable
        );
    }

    private static void modifyLootTable(RegistryKey<LootTable> key, LootTable.Builder tableBuilder, LootTableSource source, RegistryWrapper.WrapperLookup registries) {
        if (
            !source.isBuiltin()
            || !key.equals(LootTables.TRIAL_CHAMBERS_REWARD_CHEST)
        ) {
            return;
        }

        LootPool.Builder poolBuilder; {
            RegistryWrapper.Impl<Enchantment> enchantmentRegistry = registries.getOrThrow(RegistryKeys.ENCHANTMENT);
            RegistryEntry<Enchantment> myEnchantEntry = enchantmentRegistry.getOrThrow(Magnetized.MAGNETIC);
            EnchantRandomlyLootFunction.Builder randomlyEnchantWithMagnetic = EnchantRandomlyLootFunction
                .builder(registries)
                .option(myEnchantEntry)
            ;

            poolBuilder = LootPool
                .builder()
                .rolls( ConstantLootNumberProvider.create(1.0f) )
                .with(
                    ItemEntry.builder(Items.ENCHANTED_BOOK).apply(randomlyEnchantWithMagnetic)
                )
            ;
        }

        tableBuilder.pool(poolBuilder);
    }
}
