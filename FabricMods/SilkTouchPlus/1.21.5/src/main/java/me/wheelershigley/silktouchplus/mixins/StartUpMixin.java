package me.wheelershigley.silktouchplus.mixins;

import me.wheelershigley.silktouchplus.SilkTouchPlus;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;

@Mixin(MinecraftServer.class)
public class StartUpMixin {
    @Shadow @Final
    private Map<RegistryKey<World>, ServerWorld> worlds;

    @Inject(
        method = "createWorlds",
        at = @At("TAIL")
    )
    public void createWorlds(
        WorldGenerationProgressListener worldGenerationProgressListener,
        CallbackInfo ci
    ) {
        if( !this.worlds.isEmpty() ) {
            SilkTouchPlus.reload(
                this.worlds.values().iterator().next().getServer()
            );
        }
    }
}
