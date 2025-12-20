package me.wheelershigley.tradesmaxxing.mixins;

import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
    value = TradeOffer.class,
    priority = 800
)
public class DemandSuppressorMixin {
    /**
     * @author Wheeler-Shigley
     * @reason Tradesmaxxing, remove Use-based price changes
     */
    @Overwrite
    public void use() {
        //this.uses = 0;
    }

    @Shadow private int demandBonus;
    /**
     * @author Wheeler-Shigley
     * @reason Tradesmaxxing, remove Demand-based price changes
     */
    @Overwrite
    public void updateDemandBonus() {
        this.demandBonus = 0;
    }
}
