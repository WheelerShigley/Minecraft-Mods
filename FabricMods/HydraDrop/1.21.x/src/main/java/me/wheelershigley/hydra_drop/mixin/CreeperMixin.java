package me.wheelershigley.hydra_drop.mixin;

import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreeperEntity.class)
public class CreeperMixin {
    @Shadow public boolean isCharged() { return false; }
    /**
     * @author Wheeler-Shigley
     * @reason Should drop heads yes
     */
    @Overwrite
    public boolean shouldDropHead() {
        return this.isCharged();
    }
}
