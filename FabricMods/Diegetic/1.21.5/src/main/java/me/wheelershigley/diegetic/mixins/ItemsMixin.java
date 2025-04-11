package me.wheelershigley.diegetic.mixins;

import me.wheelershigley.diegetic.items.Clock;
import me.wheelershigley.diegetic.items.Compass;
import me.wheelershigley.diegetic.items.RecoveryCompass;
import me.wheelershigley.diegetic.items.Slimeball;
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

@Mixin(ServerPlayNetworkHandler.class)
public class ItemsMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(
        method = "onPlayerInteractItem",
        at = @At("HEAD")
    )
    public void onPlayerInteractItem(PlayerInteractItemC2SPacket packet, CallbackInfo ci) {

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

    }

}
