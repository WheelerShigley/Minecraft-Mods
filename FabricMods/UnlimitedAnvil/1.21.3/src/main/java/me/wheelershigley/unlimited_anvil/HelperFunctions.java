package me.wheelershigley.unlimited_anvil;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Predicate;

public class HelperFunctions {
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }

    public static <T> boolean contains(T[] set, T element) {
        if(set == null) { return false; }

        for(T subset : set) {
            if(subset.equals(element) ) {
                return true;
            }
        }
        return false;
    }

    public static ItemEnchantmentsComponent combineEnchants(ItemStack PrimaryItemStack, ItemStack SecondaryItemStack) {
        ItemEnchantmentsComponent PrimaryEnchantments = EnchantmentHelper.getEnchantments(PrimaryItemStack);
        ItemEnchantmentsComponent SecondaryEnchantments = EnchantmentHelper.getEnchantments(SecondaryItemStack);

        ItemEnchantmentsComponent.Builder ResultBuilder = new ItemEnchantmentsComponent.Builder(PrimaryEnchantments);
        Set<RegistryEntry<Enchantment>> SecondaryEnchants = SecondaryEnchantments.getEnchantments();
        int previous_level, current_level;
        for(RegistryEntry<Enchantment> SecondaryEnchant : SecondaryEnchants) {
            //TODO only allow item-specific enchants
            previous_level = PrimaryEnchantments.getLevel(SecondaryEnchant);
            current_level = SecondaryEnchantments.getLevel(SecondaryEnchant);

            /*Combine enchants if appropriate*/
            current_level = (previous_level == current_level) ? current_level+1 : Math.max(previous_level,current_level);
            /*Truncate enchant-levels by maximum-effective levels*/
            Identifier EnchantIdentifier = null;
            if( SecondaryEnchant.getKey().isPresent() ) {
                EnchantIdentifier = SecondaryEnchant.getKey().get().getValue();
            }
            if(EnchantIdentifier != null) {
                current_level = Math.min( current_level, MaximumEffectiveEnchantLevels.get(EnchantIdentifier) );
            }

            ResultBuilder.set(SecondaryEnchant, current_level);
        }

        //return sortEnchants( ResultBuilder.build() );
        return ResultBuilder.build();
    }

    /*public static ItemEnchantmentsComponent sortEnchants(ItemEnchantmentsComponent ItemEnchantments) {
        ArrayList< RegistryEntry<Enchantment> > SortedEnchantIdentifiers = new ArrayList<>();

        *//*Add all to list, to be sorted*//*
        for(RegistryEntry<Enchantment> enchantEntry : ItemEnchantments.getEnchantments() ) {
            if( enchantEntry.getKey().isEmpty() ) { continue; }
            SortedEnchantIdentifiers.add(enchantEntry);
        }

        *//*Sort the list*//*
        SortedEnchantIdentifiers.sort(
            (first, second) -> {
                if( first.getKey().isEmpty() || second.getKey().isEmpty() ) { return 0; }

                String firstName = first.getKey().get().getValue().toTranslationKey();
                String secondName = second.getKey().get().getValue().toTranslationKey();
                return firstName.compareTo(secondName);
            }
        );

        ItemEnchantmentsComponent.Builder sortedEnchants = new ItemEnchantmentsComponent.Builder(ItemEnchantments);
        *//*Remove all entries*//*
        for(RegistryEntry<Enchantment> PossibleEnchant : sortedEnchants.getEnchantments() ) {
            //Setting the value <= 0 removes the entry
            sortedEnchants.set(PossibleEnchant, 0);
        }
        *//*Add them back, in order (based on the sorted list)*//*
        for(RegistryEntry<Enchantment> SortedEnchantIdentifier : SortedEnchantIdentifiers) {
            sortedEnchants.add(
                SortedEnchantIdentifier,
                ItemEnchantments.getLevel(SortedEnchantIdentifier )
            );
        }

        return sortedEnchants.build();
    }*/

    public static int getEnchantingCost(ItemStack enchantedItem) {
        int accumulator = 0;
        for(RegistryEntry<Enchantment> enchant : enchantedItem.getEnchantments().getEnchantments() ) {
            accumulator +=
                enchant.value().getAnvilCost() * enchantedItem.getEnchantments().getLevel(enchant)
            ;
        }
        return accumulator;
    }

    public static HashMap<Identifier, Integer> MaximumEffectiveEnchantLevels = new HashMap<>(); static {
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
}
