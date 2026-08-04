package me.wheelershigley.www.charged.mixin;

import me.wheelershigley.www.charged.gamerules.WashingGameRulePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.wheelershigley.www.charged.gamerules.GameRuleRegistrar.ENABLE_PLAYER_HEAD_TEXTURE_WASHING;

@Mixin(PlayerList.class)
public class PlayerJoinMixin {
    @Inject(
        method = "placeNewPlayer",
        at = @At("TAIL")
    )
    public void onPlayerConnect(
        Connection connection,
        ServerPlayer player,
        CommonListenerCookie cookie,
        CallbackInfo ci
    ) {
        if( player.level() instanceof ServerLevel serverLevel ) {
            WashingGameRulePayload payload = new WashingGameRulePayload(
                serverLevel.getGameRules().get(ENABLE_PLAYER_HEAD_TEXTURE_WASHING)
            );
            ServerPlayNetworking.send(player, payload);
        }
    }
}
