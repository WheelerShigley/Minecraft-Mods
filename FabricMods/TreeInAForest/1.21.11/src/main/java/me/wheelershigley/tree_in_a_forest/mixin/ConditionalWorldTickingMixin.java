package me.wheelershigley.tree_in_a_forest.mixin;

import me.wheelershigley.tree_in_a_forest.TreeInAForest;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.rule.GameRules;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ConditionalWorldTickingMixin extends World  {
    protected ConditionalWorldTickingMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Shadow @Final
    private ServerWorldProperties worldProperties;

    @Shadow
    public void setTimeOfDay(long timeOfDay) {}

    @Shadow @Final private MinecraftServer server;

    @Shadow @NotNull public abstract MinecraftServer getServer();

    @Inject(
        method = "tickTime",
        at = @At("HEAD")
    )
    private void tickTime(CallbackInfo ci) {
        if(
            this.worldProperties.getGameRules().getValue(GameRules.ADVANCE_TIME)
            && TreeInAForest.serverHasOnlyBlacklistedPlayers
        ) {
            this.setTimeOfDay(this.properties.getTimeOfDay() - 1L);
        }
    }
}
