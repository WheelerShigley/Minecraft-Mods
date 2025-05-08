package me.wheelershigley.bleu.mixins;

import me.wheelershigley.bleu.Bleu;
import me.wheelershigley.bleu.RarityGameRule;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class ServerStartupRaritySavingMixin {
    @Shadow public abstract GameRules getGameRules();

    @Inject(
        method = "createWorlds",
        at = @At("TAIL")
    )
    void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        Bleu.BLUE_VARIANT_RARITY = this.getGameRules().get(RarityGameRule.BLUE_VARIANT_RARITY).get();
    }
}
