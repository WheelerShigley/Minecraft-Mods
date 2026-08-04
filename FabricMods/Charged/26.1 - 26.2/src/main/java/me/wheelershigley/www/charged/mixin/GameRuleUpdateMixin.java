package me.wheelershigley.www.charged.mixin;

import me.wheelershigley.www.charged.gamerules.WashingGameRulePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.gamerules.GameRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.wheelershigley.www.charged.gamerules.GameRuleRegistrar.ENABLE_PLAYER_HEAD_TEXTURE_WASHING;

@Mixin(MinecraftServer.class)
public abstract class GameRuleUpdateMixin {
    @Shadow public abstract Iterable<ServerLevel> getAllLevels();

    @Shadow public abstract PlayerList getPlayerList();

    @Inject(
        method = "onGameRuleChanged",
        at = @At("HEAD")
    )
    public <T> void onGameRuleUpdated(GameRule<T> gameRule, T object, CallbackInfo ci) {
        if( gameRule.equals(ENABLE_PLAYER_HEAD_TEXTURE_WASHING) ) {
            this.getAllLevels().forEach(
                (serverWorld) -> {
                    sendGameRuleUpdate(serverWorld, ENABLE_PLAYER_HEAD_TEXTURE_WASHING);
                }
            );
        }
    }

    @Unique
    public void sendGameRuleUpdate(ServerLevel world, GameRule<Boolean> gameRule) {
        boolean isWashingAllowed = world.getGameRules().get(gameRule);
        WashingGameRulePayload payload = new WashingGameRulePayload(isWashingAllowed);

        for(ServerPlayer player : this.getPlayerList().getPlayers() ) {
            ServerPlayNetworking.send(player, payload);
        }
    }

}
