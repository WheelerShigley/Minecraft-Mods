package me.wheelershigley.diegetic.mixins;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.wheelershigley.diegetic.Diegetic.*;
import static me.wheelershigley.diegetic.gamerules.DiegeticGameRules.*;

@Mixin(MinecraftServer.class)
public abstract class GameRuleInitializationOnStartMixin {
    @Shadow public abstract GameRules getGameRules();

    @Inject(
        method = "createWorlds",
        at = @At("TAIL")
    )
    void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        GameRules gamerules = this.getGameRules();

        diegeticClockDisplaysTime = gamerules.getBoolean(CLOCK_DISPLAYS_TIME);
        diegeticClockUsesServerTime = gamerules.getBoolean(CLOCK_USES_REAL_TIME);
        diegeticCompassCoordinates = gamerules.getBoolean(COMPASS_COORDINATES);
        diegeticLodestoneCompassRelativeCoordinates = gamerules.getBoolean(LODESTONE_COMPASS_RELATIVE_COORDINATES);
        diegeticRecoveryCompassRelativeCoordinates = gamerules.getBoolean(RECOVERY_COMPASS_RELATIVE_COORDINATES);
        diegeticSlimeChunkChecking = gamerules.getBoolean(SLIME_CHUNK_CHECKING);
    }
}
