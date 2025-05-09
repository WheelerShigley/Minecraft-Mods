package me.wheelershigley.tree_in_a_forest.mixin;

import me.wheelershigley.tree_in_a_forest.TreeInAForest;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class PauseServerOnStartMixin {
    @Inject(
        method = "createWorlds",
        at = @At("TAIL")
    )
    private void createWorlds(
        WorldGenerationProgressListener worldGenerationProgressListener,
        CallbackInfo ci
    ) {
        TreeInAForest.updateServerTicking();
    }
}
