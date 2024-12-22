package me.wheelershigley.tradesmaxxing.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TradeOffers.BuyItemFactory.class)
public class TradeOffersBuyItemFactoryMixin {
    @Shadow @Final private TradedItem stack;
    @Shadow @Final private int experience;
    @Shadow @Final private int price;
    @Shadow @Final private float multiplier;

    /**
     * @author Wheeler-Shigley
     * @reason Tradesmaxxing
     */
    @Overwrite
    public TradeOffer create(Entity entity, Random random) {
        return new TradeOffer(
            this.stack,
            new ItemStack( Items.EMERALD, this.price ),
            Integer.MAX_VALUE,
            this.experience,
            this.multiplier
        );
    }
}
