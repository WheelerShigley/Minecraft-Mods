package me.wheelershigley.tradesmaxxing.mixins;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TradeOffers.SellSuspiciousStewFactory.class)
public class TradeOffersSellSuspiciousStewFactoryMixin {
    @Shadow @Final private SuspiciousStewEffectsComponent stewEffects;
    @Shadow @Final private int experience;
    @Shadow @Final private float multiplier;

    /**
     * @author Wheeler-Shigley
     * @reason Tradesmaxxing
     */
    @Overwrite
    @Nullable
    public TradeOffer create(Entity entity, Random random) {
        ItemStack itemStack = new ItemStack(Items.SUSPICIOUS_STEW, 1);
        itemStack.set(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, this.stewEffects);
        return new TradeOffer(
            new TradedItem(Items.EMERALD),
            itemStack,
            Integer.MAX_VALUE,
            this.experience,
            this.multiplier
        );
    }
}