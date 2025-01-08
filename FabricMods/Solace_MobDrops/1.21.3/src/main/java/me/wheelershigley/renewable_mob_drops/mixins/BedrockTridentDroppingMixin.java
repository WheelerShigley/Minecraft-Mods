package me.wheelershigley.renewable_mob_drops.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(DrownedEntity.class)
public abstract class BedrockTridentDroppingMixin extends ZombieEntity {
    public BedrockTridentDroppingMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    /*
     * @author Wheeler-Shigley
     * @reason Bedrock-parity Trident dropping
     */
    @Inject( method = "Lnet/minecraft/entity/mob/DrownedEntity;<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("TAIL") )
    public void DrownedEntity(EntityType entityType, World world, CallbackInfo ci) {
        Arrays.fill(this.handDropChances, 0.25F);
    }
}
