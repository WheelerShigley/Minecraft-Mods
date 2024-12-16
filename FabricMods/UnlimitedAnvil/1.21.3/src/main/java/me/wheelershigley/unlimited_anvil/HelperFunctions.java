package me.wheelershigley.unlimited_anvil;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.*;

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
            previous_level = PrimaryEnchantments.getLevel(SecondaryEnchant);
            current_level = SecondaryEnchantments.getLevel(SecondaryEnchant);
            ResultBuilder.set(
                SecondaryEnchant,
                //TODO: trunc by maximum
                (previous_level == current_level) ? current_level+1 : Math.max(previous_level,current_level)
            );
        }

        return ResultBuilder.build();
    }

    public static int getEnchantingCost(ItemStack enchantedItem) {
        int accumulator = 0;
        for(RegistryEntry<Enchantment> enchant : enchantedItem.getEnchantments().getEnchantments() ) {
            accumulator +=
                enchant.value().getAnvilCost() * enchantedItem.getEnchantments().getLevel(enchant)
            ;
        }
        return accumulator;
    }


}
