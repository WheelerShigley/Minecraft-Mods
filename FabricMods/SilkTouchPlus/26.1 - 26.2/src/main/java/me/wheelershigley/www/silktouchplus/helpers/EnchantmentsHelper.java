package me.wheelershigley.www.silktouchplus.helpers;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Set;

public class EnchantmentsHelper {
    public static boolean hasSilkTouch(ServerLevel level, ItemInstance itemStack) {
        Holder<Enchantment> silkTouch = level
            .registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.SILK_TOUCH)
        ;

        int silkTouchLevel = itemStack
            .getOrDefault(
                DataComponents.ENCHANTMENTS,
                ItemEnchantments.EMPTY
            )
            .getLevel(silkTouch)
        ;

        return 0 < silkTouchLevel;
    }

    public static boolean includesEnchantment(
        Set<  Object2IntMap.Entry< Holder<Enchantment> >  > enchants,
        ResourceKey<Enchantment> reference
    ) {
        for(Object2IntMap.Entry< Holder<Enchantment> > enchant : enchants) {
            if( enchant.getKey().is(reference) ) {
                return true;
            }
        }
        return false;
    }
}
