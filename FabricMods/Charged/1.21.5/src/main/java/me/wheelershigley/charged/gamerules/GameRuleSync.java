package me.wheelershigley.charged.gamerules;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

import java.util.function.BiConsumer;

public class GameRuleSync {
    public static BiConsumer<MinecraftServer, GameRules.BooleanRule> commonSideWashingSync = (server, rule) -> {
        boolean washingEnabled = server.getGameRules().getBoolean(GameRuleRegistrar.ENABLE_PLAYER_HEAD_TEXTURE_WASHING);
        PacketByteBuf packet = PacketByteBufs.create();
        packet.writeBoolean(washingEnabled);

        WashingGameRulePayload payload = new WashingGameRulePayload(washingEnabled);
        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList() ) {
            ServerPlayNetworking.send(player, payload);
        }
    };
}
