package me.wheelershigley.www.tree_in_a_forest.mixin;

import me.wheelershigley.www.tree_in_a_forest.TreeInAForest;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.wheelershigley.www.tree_in_a_forest.TreeInAForest.gameProfileCache;

@Mixin(PlayerList.class)
public class PlayerConnectionMixin {

    @Inject(
        method = "placeNewPlayer",
        at = @At("TAIL")
    )
    public void onPlayerConnect(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo ci) {
        if(  !gameProfileCache.containsKey( player.getUUID() )  ) {
            gameProfileCache.put(
                player.getUUID(),
                player.nameAndId()
            );
        }
        TreeInAForest.updateServerTicking();
    }

    @Inject(
        method = "remove",
        at = @At("TAIL")
    )
    public void remove(ServerPlayer player, CallbackInfo ci) {
        TreeInAForest.updateServerTicking();
    }
}
