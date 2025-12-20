package me.wheelershigley.tradesmaxxing.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
    value = TradeOffers.SellMapFactory.class,
    priority = 800
)
public class SellMapFactoryUseMixin {
    @Shadow @Final private int maxUses;
    @Shadow @Final private int experience;

    @Inject(
        method = "create",
        at = @At("RETURN"),
        cancellable = true
    )
    public void create(
        ServerWorld world,
        Entity entity,
        Random random,
        CallbackInfoReturnable<TradeOffer> cir
    ) {
        TradeOffer originalResult = cir.getReturnValue();
        if(originalResult == null) {
            return;
        }

        TradeOffer modifiedResult = new TradeOffer(
            originalResult.getFirstBuyItem(),
            originalResult.getSecondBuyItem(),
            originalResult.getSellItem(),
            Integer.MAX_VALUE, //this.maxUses,
            this.experience,
            0.2F
        );
        cir.setReturnValue(modifiedResult);
    }
}
