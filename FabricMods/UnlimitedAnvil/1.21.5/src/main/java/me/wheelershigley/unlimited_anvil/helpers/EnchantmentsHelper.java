package me.wheelershigley.unlimited_anvil.helpers;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Set;

public class EnchantmentsHelper {
    private static final HashMap<Identifier, Integer> MaximumEffectiveEnchantLevels; static {
        MaximumEffectiveEnchantLevels = new HashMap<>();
        MaximumEffectiveEnchantLevels.put(Enchantments.AQUA_AFFINITY        .getValue(), 1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.BANE_OF_ARTHROPODS   .getValue(), Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.BLAST_PROTECTION     .getValue(), 10                 );
        MaximumEffectiveEnchantLevels.put(Enchantments.BREACH               .getValue(), Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.CHANNELING           .getValue(), 1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.BINDING_CURSE        .getValue(), 1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.VANISHING_CURSE      .getValue(), 1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.DENSITY              .getValue(), Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.DEPTH_STRIDER        .getValue(), 3                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.EFFICIENCY           .getValue(), 255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.FEATHER_FALLING      .getValue(), 7                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.FIRE_ASPECT          .getValue(), 255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.FIRE_PROTECTION      .getValue(), 10                 );
        MaximumEffectiveEnchantLevels.put(Enchantments.FLAME                .getValue(), 1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.FORTUNE              .getValue(), 255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.FROST_WALKER         .getValue(), 14                 );
        MaximumEffectiveEnchantLevels.put(Enchantments.IMPALING             .getValue(), Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.INFINITY             .getValue(), 1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.KNOCKBACK            .getValue(), 255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.LOOTING              .getValue(), 255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.LOYALTY              .getValue(), 127                );
        MaximumEffectiveEnchantLevels.put(Enchantments.LUCK_OF_THE_SEA      .getValue(), 255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.LURE                 .getValue(), 5                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.MENDING              .getValue(), 1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.MULTISHOT            .getValue(), 1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.PIERCING             .getValue(), 127                );
        MaximumEffectiveEnchantLevels.put(Enchantments.POWER                .getValue(), 255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.PROJECTILE_PROTECTION.getValue(), 10                 );
        MaximumEffectiveEnchantLevels.put(Enchantments.PROTECTION           .getValue(), 20                 );
        MaximumEffectiveEnchantLevels.put(Enchantments.PUNCH                .getValue(), 255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.QUICK_CHARGE         .getValue(), 5                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.RESPIRATION          .getValue(), 255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.RIPTIDE              .getValue(), 255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.SHARPNESS            .getValue(), Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.SILK_TOUCH           .getValue(), 1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.SMITE                .getValue(), Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.SOUL_SPEED           .getValue(), 255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.SWEEPING_EDGE        .getValue(), 255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.SWIFT_SNEAK          .getValue(), 5                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.THORNS               .getValue(), Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.UNBREAKING           .getValue(), 255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.WIND_BURST           .getValue(), 255                );
    }

    public static int getMaximumEffectiveLevel(Identifier Enchantment) {
        if( MaximumEffectiveEnchantLevels.containsKey(Enchantment) ) {
            return MaximumEffectiveEnchantLevels.get(Enchantment);
        }
        return Integer.MIN_VALUE;
    }

    private static int getStoredEnchantmentLevel(RegistryEntry<Enchantment> enchantment, ItemStack stack, boolean useStoredEnchants) {
        ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(
                useStoredEnchants ? DataComponentTypes.STORED_ENCHANTMENTS : DataComponentTypes.ENCHANTMENTS,
                ItemEnchantmentsComponent.DEFAULT
        );
        return itemEnchantmentsComponent.getLevel(enchantment);
    }

    public static int getEnchantingCost(ItemStack enchantedItem) {
        boolean useStoredEnchantments = enchantedItem.contains(DataComponentTypes.STORED_ENCHANTMENTS);

        int accumulator = 0;
        Set<RegistryEntry<Enchantment>> Enchantments = net.minecraft.enchantment.EnchantmentHelper.getEnchantments(enchantedItem).getEnchantments();
        for(RegistryEntry<Enchantment> enchant : Enchantments) {
            accumulator += enchant.value().getAnvilCost() * getStoredEnchantmentLevel(enchant, enchantedItem, useStoredEnchantments);
        }
        return 2*accumulator;
    }
}
