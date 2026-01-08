package me.wheelershigley.www.magnetized;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
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
        registerItemsToCreativeMenu();
    }

    private static void modifyLootTable(
        RegistryKey<LootTable> key,
        LootTable.Builder tableBuilder,
        LootTableSource source,
        RegistryWrapper.WrapperLookup registries
    ) {
        if(
            !source.isBuiltin()
            || !key.equals(LootTables.TRIAL_CHAMBERS_REWARD_CHEST)
        ) {
            return;
        }

        LootPool.Builder poolBuilder; {
            RegistryWrapper.Impl<Enchantment> enchantmentRegistry = registries.getOrThrow(RegistryKeys.ENCHANTMENT);
            RegistryEntry<Enchantment> myEnchantEntry = enchantmentRegistry.getOrThrow(Magnetized.MAGNETIC);
            EnchantRandomlyLootFunction.Builder enchantFunction = EnchantRandomlyLootFunction
                .builder(registries)
                .option(myEnchantEntry)
            ;

            LootCondition.Builder chance = RandomChanceLootCondition.builder(0.50f);

            poolBuilder = LootPool
                .builder()
                .rolls( ConstantLootNumberProvider.create(1.0f) )
                .conditionally(chance)
                .with(
                    ItemEntry
                        .builder(Items.ENCHANTED_BOOK)
                        .apply(enchantFunction)
                )
            ;
        }

        tableBuilder.pool(poolBuilder);
    }

    private static void registerItemsToCreativeMenu() {
        ItemGroupEvents
            .modifyEntriesEvent(ItemGroups.INGREDIENTS)
            .register(
                (content) -> {
                    RegistryWrapper.Impl<Enchantment> enchantmentRegistry = content.getContext().lookup().getOrThrow(RegistryKeys.ENCHANTMENT);
                    RegistryEntry<Enchantment> myEnchantEntry = enchantmentRegistry.getOrThrow(Magnetized.MAGNETIC);

                    for(int level = 1; level <= 3; level++) {
                        content.add(
                            getEnchantedBookItemStack(myEnchantEntry, level)
                        );
                    }
                }
            );
    }

    private static ItemStack getEnchantedBookItemStack(RegistryEntry<Enchantment> enchantmentRegistryEntry, int level) {
        return EnchantmentHelper.getEnchantedBookWith(
            new EnchantmentLevelEntry(enchantmentRegistryEntry, level)
        );
    }
}
