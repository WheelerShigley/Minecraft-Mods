package me.wheelershigley.charged.mixins;

import me.wheelershigley.charged.Charged;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreeperEntity.class)
public class CreeperMixin {
    @Shadow private int headsDropped;
    @Shadow public boolean isCharged() { return false; }
    /**
     * @author Wheeler-Shigley
     * @reason Should drop heads yes
     */
    @Overwrite
    public boolean shouldDropHead() {
        int maximum_head_drops_count = (int)( (long)Charged.configurations.getConfiguration("MaximumDropsPerChargedCreeper").getValue() );
        if(maximum_head_drops_count < 0) {
            maximum_head_drops_count = Integer.MAX_VALUE;
        }

        return this.isCharged() && headsDropped < maximum_head_drops_count;
    }
}
