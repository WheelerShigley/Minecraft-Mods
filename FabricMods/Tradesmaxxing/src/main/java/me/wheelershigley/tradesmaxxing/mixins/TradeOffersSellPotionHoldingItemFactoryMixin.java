package me.wheelershigley.tradesmaxxing.mixins;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(TradeOffers.SellPotionHoldingItemFactory.class)
public class TradeOffersSellPotionHoldingItemFactoryMixin {
    @Shadow @Final private ItemStack sell;
    @Shadow @Final private int sellCount;
    @Shadow @Final private int price;
    @Shadow @Final private int maxUses;
    @Shadow @Final private int experience;
    @Shadow @Final private Item secondBuy;
    @Shadow @Final private int secondCount;
    @Shadow @Final private float priceMultiplier;

    /**
     * @author Wheeler-Shigley
     * @reason Tradesmaxxing
     */
    @Overwrite
    public TradeOffer create(Entity entity, Random random) {
        TradedItem tradedItem = new TradedItem(Items.EMERALD, this.price);
        /*Get a random potion*/
        List< RegistryEntry<Potion> > list = Registries.POTION.streamEntries().filter(
            (entry) -> {
                return
                       !( entry.value() ).getEffects().isEmpty()
                    && entity.getWorld().getBrewingRecipeRegistry().isBrewable(entry)
                ;
            }
        ).collect( Collectors.toList() );
        RegistryEntry<Potion> registryEntry = Util.getRandom(list, random);

        ItemStack itemStack = new ItemStack(this.sell.getItem(), this.sellCount);
        itemStack.set( DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(registryEntry) );
        return new TradeOffer(
            tradedItem,
            Optional.of( new TradedItem(this.secondBuy, this.secondCount) ),
            itemStack,
            Integer.MAX_VALUE,
            this.experience,
            this.priceMultiplier
        );
    }
}
