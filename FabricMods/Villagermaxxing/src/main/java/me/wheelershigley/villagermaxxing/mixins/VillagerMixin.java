package me.wheelershigley.villagermaxxing.mixins;

import me.wheelershigley.villagermaxxing.Villagermaxxing;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public class VillagerMixin {
    @Inject(method = "beginTradeWith", at = @At("HEAD") )
    private void beginTradeWith(PlayerEntity customer, CallbackInfo ci) {
        Villagermaxxing.LOGGER.info( customer.toString() );
    }
}
