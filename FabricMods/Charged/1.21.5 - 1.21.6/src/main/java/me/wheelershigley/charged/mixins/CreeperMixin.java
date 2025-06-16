package me.wheelershigley.charged.mixins;

import me.wheelershigley.charged.gamerules.GameRuleRegistrar;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreeperEntity.class)
public class CreeperMixin extends HostileEntity {
    protected CreeperMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow private int headsDropped;
    @Shadow public boolean isCharged() { return false; }
    /**
     * @author Wheeler-Shigley
     * @reason Should drop heads yes
     */
    @Overwrite
    public boolean shouldDropHead() {
        int maximum_head_drops_count = 0; {
            MinecraftServer server = this.getWorld().getServer();
            if(server != null) {
                maximum_head_drops_count = server.getGameRules().get(GameRuleRegistrar.MAXIMUM_HEAD_DROP_COUNT).get();
            }
            if(maximum_head_drops_count < 0) {
                maximum_head_drops_count = Integer.MAX_VALUE;
            }
        }
        return this.isCharged() && headsDropped < maximum_head_drops_count;
    }
}
