package me.wheelershigley.www.charged.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.wheelershigley.www.charged.gamerules.GameRuleRegistrar.MAXIMUM_HEAD_DROP_COUNT;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

@Mixin(Creeper.class)
public class CreeperMixin extends Monster {
    protected CreeperMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Unique
    private int headDropCount;
    @Shadow
    private boolean droppedSkulls;

    @Shadow
    public boolean isPowered() { return false; }

    /**
     * @author Wheeler-Shigley
     * @reason Should drop heads yes
     */
    @Inject(
        method = "killedEntity",
        at = @At("HEAD")
    )
    public void onKilledOther(
        ServerLevel world,
        LivingEntity other,
        DamageSource damageSource,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if(
            this.shouldDropLoot(world)
            && this.isPowered()
        ) {
            headDropCount++;
            droppedSkulls = false;
        }

        //weather heads are drops or not is controlled by "headsDropped"
        final int maximum_head_drops_count = world.getGameRules().get(MAXIMUM_HEAD_DROP_COUNT);
        if(maximum_head_drops_count < 0) {
            return;
        }
        if(maximum_head_drops_count < headDropCount) {
            droppedSkulls = true;
        }
    }
}
