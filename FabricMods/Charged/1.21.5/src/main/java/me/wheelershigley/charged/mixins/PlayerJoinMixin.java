package me.wheelershigley.charged.mixins;

import me.wheelershigley.charged.Charged;
import me.wheelershigley.charged.gamerules.GameRuleRegistrar;
import me.wheelershigley.charged.gamerules.GameRuleSync;
import me.wheelershigley.charged.gamerules.WashingGameRulePayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.wheelershigley.charged.gamerules.GameRuleRegistrar.ENABLE_PLAYER_HEAD_TEXTURE_WASHING;

@Mixin(PlayerManager.class)
public class PlayerJoinMixin {
    @Inject(
        method = "onPlayerConnect",
        at = @At("TAIL")
    )
    public void onPlayerConnect(
        ClientConnection connection,
        ServerPlayerEntity player,
        ConnectedClientData clientData,
        CallbackInfo ci
    ) {
        WashingGameRulePayload payload = new WashingGameRulePayload(
            player.server.getGameRules().getBoolean(ENABLE_PLAYER_HEAD_TEXTURE_WASHING)
        );
        ServerPlayNetworking.send(player, payload);
    }
}
