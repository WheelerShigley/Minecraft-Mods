package me.wheelershigley.trampleless.mixin;

import me.wheelershigley.trampleless.Trampleless;
import me.wheelershigley.trampleless.TramplelessGameRules;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.wheelershigley.trampleless.TramplelessGameRules.*;

@Mixin(MinecraftServer.class)
public abstract class rememberGameRulesOnStartUpMixin {
    @Shadow @Nullable public abstract ServerWorld getWorld(RegistryKey<World> key);

    @Shadow public abstract Iterable<ServerWorld> getWorlds();

    @Shadow public abstract GameRules getGameRules();

    @Inject(
        method = "createWorlds",
        at = @At("TAIL")
    )
    void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        GameRules gameRules = this.getGameRules();

        Trampleless.farmlandTrampling = gameRules.getBoolean(FARMLAND_TRAMPLING);
        Trampleless.featherFallingTrampling = gameRules.getBoolean(FEATHER_FALLING_TRAMPLING);
    }
}
