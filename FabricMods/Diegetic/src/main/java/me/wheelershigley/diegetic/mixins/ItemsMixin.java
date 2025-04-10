package me.wheelershigley.diegetic.mixins;

import me.wheelershigley.diegetic.Diegetic;
import me.wheelershigley.diegetic.imeplementations.Clock;
import me.wheelershigley.diegetic.imeplementations.Compass;
import me.wheelershigley.diegetic.imeplementations.RecoveryCompass;
import me.wheelershigley.diegetic.imeplementations.Slimeball;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerPlayNetworkHandler.class)
public class ItemsMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(
        method = "onPlayerInteractItem",
        at = @At("HEAD")
    )
    public void onPlayerInteractItem(PlayerInteractItemC2SPacket packet, CallbackInfo ci) {
        UUID playerUuid = this.player.getUuid();
        if(
            Diegetic.LastUsageMap.containsKey(playerUuid)
            && player.getWorld().getServer() != null
            && ( player.getWorld().getServer().getTimeReference() - Diegetic.LastUsageMap.get(playerUuid) ) < Diegetic.COOLDOWN_TICKS
        ) {
            return;
        }

        ItemStack itemStack = this.player.getStackInHand( packet.getHand() );
        if( itemStack.getItem().equals(Items.CLOCK) ) {
            Clock.use(this.player);
        }
        if( itemStack.getItem().equals(Items.COMPASS) ) {
            Compass.use(this.player, itemStack);
        }
        if( itemStack.getItem().equals(Items.RECOVERY_COMPASS) ) {
            RecoveryCompass.use(this.player);
        }
        if( itemStack.getItem().equals(Items.SLIME_BALL) ) {
            Slimeball.use(this.player);
        }

        if(player.getWorld().getServer() != null) {
            Diegetic.LastUsageMap.put(
                playerUuid,
                player.getWorld().getServer().getTimeReference()
            );
        }
    }

}
