package me.wheelershigley.tradesmaxxing.mixins;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(TradeOffers.EnchantBookFactory.class)
public class TradeOffersEnchantBookFactoryMixin {
    @Shadow @Final private int experience;
    @Shadow @Final private TagKey<Enchantment> possibleEnchantments;
    @Shadow @Final private int maxLevel;

    /**
     * @author Wheeler-Shigley
     * @reason Tradesmaxxing
     */
    @Overwrite
    public TradeOffer create(Entity entity, Random random) {
        Optional<RegistryEntry<Enchantment>> optional = entity.getWorld().getRegistryManager()
            .getOrThrow(RegistryKeys.ENCHANTMENT)
            .getRandomEntry(this.possibleEnchantments, random)
        ;
        int level;
        ItemStack itemStack;
        if( !optional.isEmpty() ) {
            RegistryEntry<Enchantment> registryEntry = (RegistryEntry)optional.get();
            Enchantment enchantment = (Enchantment)registryEntry.value();
            level = Math.min(enchantment.getMaxLevel(), this.maxLevel);
            itemStack = EnchantmentHelper.getEnchantedBookWith( new EnchantmentLevelEntry(registryEntry, level) );
            level = 2 + 3*level;
            if( registryEntry.isIn(EnchantmentTags.DOUBLE_TRADE_PRICE) ) {
                level *= 2;
            }
        } else {
            itemStack = new ItemStack(Items.BOOK);
            level = 1;
        }

        return new TradeOffer(
            new TradedItem(Items.EMERALD, level),
            Optional.of( new TradedItem(Items.BOOK) ),
            itemStack,
            Integer.MAX_VALUE,
            this.experience,
            0.2F
        );
    }
}