package me.wheelershigley.charged.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.wheelershigley.charged.gamerules.GameRuleRegistrar.MAXIMUM_HEAD_DROP_COUNT;

@Mixin(CreeperEntity.class)
public class CreeperMixin extends HostileEntity {
    protected CreeperMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private int headDropCount;
    @Shadow
    private boolean headsDropped;

    @Shadow
    public boolean isCharged() { return false; }

    /**
     * @author Wheeler-Shigley
     * @reason Should drop heads yes
     */
    @Inject(
        method = "onKilledOther",
        at = @At("HEAD")
    )
    public void onKilledOther(
        ServerWorld world,
        LivingEntity other,
        DamageSource damageSource,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if(
            this.shouldDropLoot(world)
            && this.isCharged()
        ) {
            headDropCount++;
            headsDropped = false;
        }

        //weather heads are drops or not is controlled by "headsDropped"
        final int maximum_head_drops_count = world.getGameRules().getValue(MAXIMUM_HEAD_DROP_COUNT);
        if(maximum_head_drops_count < 0) {
            return;
        }
        if(maximum_head_drops_count < headDropCount) {
            headsDropped = true;
        }
    }
}
