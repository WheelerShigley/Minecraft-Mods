package me.wheelershigley.www.tree_in_a_forest.mixin;

import me.wheelershigley.www.tree_in_a_forest.TreeInAForest;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ConditionalWorldTickingMixin extends Level  {
    protected ConditionalWorldTickingMixin(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Shadow
    public GameRules getGameRules() { return null; }
    @Shadow @NotNull public abstract MinecraftServer getServer();

    @Inject(
        method = "tickTime",
        at = @At("HEAD"),
        cancellable = true
    )
    private void tickTime(CallbackInfo ci) {
        if(
            this.getGameRules().get(GameRules.ADVANCE_TIME)
            && TreeInAForest.serverHasOnlyBlacklistedPlayers
        ) {
            ci.cancel();
        }
    }
}
