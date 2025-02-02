package me.wheelershigley.tradesmaxxing.server.mixins;

import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = TradeOffer.class, priority = 800)
public class DemandSuppressorMixin {
    //@Shadow private int uses;
    /**
     * @author Wheeler-Shigley
     * @reason Tradesmaxxing
     */
    @Overwrite
    public void use() {
        //this.uses = 0;
    }

    @Shadow private int demandBonus;
    /**
     * @author Wheeler-Shigley
     * @reason Tradesmaxxing
     */
    @Overwrite
    public void updateDemandBonus() {
        this.demandBonus = 0;
    }
}
