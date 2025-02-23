package me.wheelershigley.silktouchplus.helpers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Set;

public class EnchantmentsHelper {
    public static boolean includesEnchantment(Set<RegistryEntry<Enchantment>> enchants, Enchantment reference) {
        for(RegistryEntry<Enchantment> enchant : enchants) {
            if( enchant.value().equals(reference) ) {
                return true;
            }
        }
        return false;
    }
}
