package me.wheelershigley.tree_in_a_forest.mixin;

import me.wheelershigley.tree_in_a_forest.TreeInAForest;
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

    @Inject(
        method = "onPlayerConnect",
        at = @At("TAIL")
    )
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        TreeInAForest.updateServerTicking();
    }

    @Inject(
        method = "remove",
        at = @At("TAIL")
    )
    public void remove(ServerPlayerEntity player, CallbackInfo ci) {
        TreeInAForest.updateServerTicking();
    }
}
