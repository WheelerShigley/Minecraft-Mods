package me.wheelershigley.tradesmaxxing.server.mixins;

import me.wheelershigley.tradesmaxxing.server.Tradesmaxxing;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
