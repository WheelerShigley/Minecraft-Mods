package me.wheelershigley.live_catch.mixins;

import me.wheelershigley.live_catch.EntityLink;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class WorldTickMixin {
    @Shadow
    public Iterable<ServerWorld> getWorlds() { return null; }

    @Inject(
        method = "tickWorlds",
        at = @At("TAIL")
    )
    protected void tickWorlds(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        EntityLink.tickAll();
    }
}
