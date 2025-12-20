package me.wheelershigley.tuxies_traders.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;

public class ItemHelpers {
    //TradeOffers::createPotionStack
    public static ItemStack createPotionStack(RegistryEntry<Potion> potion) {
        return PotionContentsComponent.createStack(Items.POTION, potion);
    }

    public static ItemStack createTippedArrowStack(RegistryEntry<Potion> potion) {
        ItemStack itemStack = new ItemStack(Items.TIPPED_ARROW);
        itemStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));
        return itemStack;
    }
}
