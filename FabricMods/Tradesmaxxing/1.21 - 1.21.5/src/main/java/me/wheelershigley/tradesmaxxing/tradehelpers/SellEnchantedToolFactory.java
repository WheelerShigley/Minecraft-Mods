package me.wheelershigley.tradesmaxxing.tradehelpers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;

import java.util.Optional;

public class SellEnchantedToolFactory implements TradeOffers.Factory {
    private final ItemStack tool;
    private final int basePrice;
    private final int experience;
    private final float multiplier;

    /*
    public SellEnchantedToolFactory(Item item, int basePrice, int experience) {
        this(item, basePrice, experience, 0.05F);
    }
     */

    public SellEnchantedToolFactory(Item item, int basePrice, int experience, float multiplier) {
        this.tool = new ItemStack(item);
        this.basePrice = basePrice;
        this.experience = experience;
        this.multiplier = multiplier;
    }

    public TradeOffer create(Entity entity, Random random) {
        DynamicRegistryManager dynamicRegistryManager = entity.getWorld().getRegistryManager();
        Optional<RegistryEntryList.Named<Enchantment>> optional = dynamicRegistryManager
            .getOrThrow(RegistryKeys.ENCHANTMENT)
            .getOptional(EnchantmentTags.ON_TRADED_EQUIPMENT);

        ItemStack itemStack = EnchantmentHelper.enchant(
            random,
            new ItemStack( this.tool.getItem() ),
            5 + random.nextInt(15),
            dynamicRegistryManager,
            optional
        );

        TradedItem tradedItem = new TradedItem(Items.EMERALD, this.basePrice);
        return new TradeOffer(
            tradedItem,
            itemStack,
            Integer.MAX_VALUE,
            this.experience,
            this.multiplier
        );
    }
}
