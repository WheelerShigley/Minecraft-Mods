package me.wheelershigley.tradesmaxxing.mixins;

import me.wheelershigley.tradesmaxxing.Tradesmaxxing;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(TradeOffers.SellEnchantedToolFactory.class)
public class TradeOffersSellEnchantedToolFactoryMixin {

    @Shadow @Final private ItemStack tool;
    @Shadow @Final private int basePrice;
    @Shadow @Final private int experience;
    @Shadow @Final private float multiplier;

    /**
     * @author Wheeler-Shigley
     * @reason Tradesmaxxing
     */
    @Overwrite
    public TradeOffer create(Entity entity, Random random) {
        Tradesmaxxing.LOGGER.info(this.basePrice +" -> "+ (this.basePrice+2) );

        DynamicRegistryManager dynamicRegistryManager = entity.getWorld().getRegistryManager();
        Optional< RegistryEntryList.Named<Enchantment> > optional = dynamicRegistryManager
            .getOrThrow(RegistryKeys.ENCHANTMENT)
            .getOptional(EnchantmentTags.ON_TRADED_EQUIPMENT)
        ;
        ItemStack itemStack = EnchantmentHelper.enchant(
            random,
            new ItemStack( this.tool.getItem() ),
            random.nextInt(15),
            dynamicRegistryManager,
            optional
        );

        return new TradeOffer(
            new TradedItem(Items.EMERALD, this.basePrice+5),
            itemStack,
            Integer.MAX_VALUE,
            this.experience,
            this.multiplier
        );
    }
}
