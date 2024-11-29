package me.wheelershigley.treeinaforest.mixins;

import com.sun.source.tree.Tree;
import me.wheelershigley.treeinaforest.TreeInAForest;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerConnectionMixin {

    @Inject( method = "onPlayerConnect", at = @At("HEAD") )
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        TreeInAForest.players.add( player.getUuid() );
        TreeInAForest.updateServerTicking();
    }

    @Inject( method = "remove", at = @At("HEAD") )
    public void remove(ServerPlayerEntity player, CallbackInfo ci) {
        TreeInAForest.players.remove( player.getUuid() );
        TreeInAForest.updateServerTicking();
    }
}
