package me.wheelershigley.charged.mixins;

import me.wheelershigley.charged.Charged;
import me.wheelershigley.charged.gamerules.GameRuleRegistrar;
import me.wheelershigley.charged.gamerules.WashingGameRulePayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.rule.GameRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.wheelershigley.charged.gamerules.GameRuleRegistrar.ENABLE_PLAYER_HEAD_TEXTURE_WASHING;

@Mixin(MinecraftServer.class)
public abstract class GameRuleUpdateMixin {
    @Shadow public abstract Iterable<ServerWorld> getWorlds();

    @Shadow public abstract PlayerManager getPlayerManager();

    @Inject(
        method = "onGameRuleUpdated",
        at = @At("HEAD")
    )
    public <T> void onGameRuleUpdated(GameRule<T> gameRule, T object, CallbackInfo ci) {
        if( gameRule.equals(ENABLE_PLAYER_HEAD_TEXTURE_WASHING) ) {
            this.getWorlds().forEach(
                (serverWorld) -> {
                    sendGameRuleUpdate(serverWorld, ENABLE_PLAYER_HEAD_TEXTURE_WASHING);
                }
            );
        }
    }

    public void sendGameRuleUpdate(ServerWorld world, GameRule<Boolean> gameRule) {
        boolean isWashingAllowed = world.getGameRules().getValue(gameRule);
        PacketByteBuf packet = PacketByteBufs.create();
        packet.writeBoolean(isWashingAllowed);

        WashingGameRulePayload payload = new WashingGameRulePayload(isWashingAllowed);
        for(ServerPlayerEntity player : this.getPlayerManager().getPlayerList() ) {
            ServerPlayNetworking.send(player, payload);
        }
    };

}
