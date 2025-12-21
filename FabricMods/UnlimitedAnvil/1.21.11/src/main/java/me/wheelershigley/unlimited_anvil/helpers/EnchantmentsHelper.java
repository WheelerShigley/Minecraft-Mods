package me.wheelershigley.unlimited_anvil.helpers;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.HashMap;
import java.util.Set;

public class EnchantmentsHelper {
    private static final HashMap<RegistryKey<Enchantment>, Integer> MaximumEffectiveEnchantLevels; static {
        MaximumEffectiveEnchantLevels = new HashMap<>();
        MaximumEffectiveEnchantLevels.put(Enchantments.AQUA_AFFINITY,       1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.BANE_OF_ARTHROPODS,  Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.BLAST_PROTECTION,    10                 );
        MaximumEffectiveEnchantLevels.put(Enchantments.BREACH,              Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.CHANNELING,          1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.BINDING_CURSE,       1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.VANISHING_CURSE,     1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.DENSITY,             Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.DEPTH_STRIDER,       3                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.EFFICIENCY,          255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.FEATHER_FALLING,     7                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.FIRE_ASPECT,         255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.FIRE_PROTECTION,     10                 );
        MaximumEffectiveEnchantLevels.put(Enchantments.FLAME,               1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.FORTUNE,             255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.FROST_WALKER,        14                 );
        MaximumEffectiveEnchantLevels.put(Enchantments.IMPALING,            Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.INFINITY,            1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.KNOCKBACK,           255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.LOOTING,             255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.LOYALTY,             127                );
        MaximumEffectiveEnchantLevels.put(Enchantments.LUCK_OF_THE_SEA,     255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.LUNGE,               Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.LURE,                5                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.MENDING,             1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.MULTISHOT,           1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.PIERCING,            127                );
        MaximumEffectiveEnchantLevels.put(Enchantments.POWER,               255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.PROJECTILE_PROTECTION, 10                 );
        MaximumEffectiveEnchantLevels.put(Enchantments.PROTECTION,          20                 );
        MaximumEffectiveEnchantLevels.put(Enchantments.PUNCH,               255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.QUICK_CHARGE,        5                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.RESPIRATION,         255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.RIPTIDE,             255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.SHARPNESS,           Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.SILK_TOUCH,          1                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.SMITE,               Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.SOUL_SPEED,          255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.SWEEPING_EDGE,       255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.SWIFT_SNEAK,         5                  );
        MaximumEffectiveEnchantLevels.put(Enchantments.THORNS,              Integer.MAX_VALUE  );
        MaximumEffectiveEnchantLevels.put(Enchantments.UNBREAKING,          255                );
        MaximumEffectiveEnchantLevels.put(Enchantments.WIND_BURST,          255                );
    }

    public static int getMaximumEffectiveLevel(RegistryKey<Enchantment> Enchantment) {
        if( MaximumEffectiveEnchantLevels.containsKey(Enchantment) ) {
            return MaximumEffectiveEnchantLevels.get(Enchantment);
        }
        return Integer.MIN_VALUE;
    }

    public static int getEnchantingCost(ItemStack enchantedItem) {
        int accumulator = 0;

        Set<RegistryEntry<Enchantment>> Enchantments = EnchantmentHelper.getEnchantments(enchantedItem).getEnchantments();
        for(RegistryEntry<Enchantment> enchant : Enchantments) {

            int enchantment_cost = enchant.value().getAnvilCost();
            int enchantment_level = getEnchantmentLevel(enchantedItem, enchant);
            accumulator += enchantment_cost * enchantment_level;
        }
        return accumulator;
    }

    private static int getEnchantmentLevel(ItemStack enchantedItem, RegistryEntry<Enchantment> enchantment) {
        ItemEnchantmentsComponent itemEnchantmentsComponent = ItemEnchantmentsComponent.DEFAULT;

        boolean usesStoredEnchantments = enchantedItem.contains(DataComponentTypes.STORED_ENCHANTMENTS);
        if(usesStoredEnchantments) {
            itemEnchantmentsComponent = enchantedItem.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        }
        if(
            !usesStoredEnchantments
            || itemEnchantmentsComponent == ItemEnchantmentsComponent.DEFAULT
        ) {
            itemEnchantmentsComponent = enchantedItem.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        }

        return itemEnchantmentsComponent.getLevel(enchantment);
    }
}
