package me.wheelershigley.tuxies_traders.mixins;

import me.wheelershigley.tuxies_traders.item.ItemGroups;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
    value = TradeOffers.SellItemFactory.class,
    priority = 600
)
public class NetheriteTradesMixin {
    @Shadow @Final private ItemStack sell;
    @Shadow @Final private int price;
    @Shadow @Final private int maxUses;
    @Shadow @Final private int experience;
    @Shadow @Final private float multiplier;

    /**
     * @author Wheeler-Shigley
     * @reason Netherite Trades, when Spawn-Eggs
     */
    @Inject(
        method = "create",
        at = @At("TAIL"),
        cancellable = true
    )
    public void create(
        ServerWorld world,
        Entity entity,
        Random random,
        CallbackInfoReturnable<TradeOffer> cir
    ) {
        if( ItemGroups.isSpawnEgg( sell.getItem() ) ) {
            TradeOffer originalResult = cir.getReturnValue();
            TradeOffer modifiedResult = new TradeOffer(
                new TradedItem(Items.NETHERITE_INGOT, this.price),
                originalResult.getSecondBuyItem(),
                this.sell,
                this.maxUses,
                this.experience,
                this.multiplier
            );

            cir.setReturnValue(modifiedResult);
        }
    }
}
