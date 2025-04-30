package me.wheelershigley.lil_guy.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TadpoleEntity.class)
public abstract class TadpoleMixin extends FishEntity {
    public TadpoleMixin(EntityType<? extends FishEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow private int tadpoleAge;
    @Shadow private void setTadpoleAge(int tadpoleAge) {}

    /**
     * @author Wheeler-Shigley
     * @reason Tadpoles are not Baby Frogs, but they should behave like them: not ageing when named.
     */
    @Overwrite
    public void tickMovement() {
        super.tickMovement();
        if(
            !this.getWorld().isClient
            && !this.hasCustomName()
        ) {
            this.setTadpoleAge(this.tadpoleAge + 1);
        }
    }
}
