package me.wheelershigley.www.solace_jobs.mixins;

import me.wheelershigley.www.solace_jobs.rewards.ActionType;
import me.wheelershigley.www.solace_jobs.rewards.RewardsHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class ShearMixin {
    @Inject(
        method = "interact",
        at = @At("HEAD")
    )
    protected void mobInteract(
        Player player,
        InteractionHand hand,
        Vec3 location,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        if( !(player instanceof ServerPlayer serverPlayer) ) {
            return;
        }
        if( !(this instanceof Shearable shearable) ) {
            return;
        }

        if( !isShearing(shearable, serverPlayer, hand) ) {
            return;
        }

        LivingEntity livingEntity = ( (Entity)(Object)this ).asLivingEntity();
        if(livingEntity == null) {
            return;
        }

        Identifier targetIdentifier = BuiltInRegistries.ENTITY_TYPE.getKey( livingEntity.getType() );
        RewardsHelper.attemptReward(
            serverPlayer,
            targetIdentifier,
            ActionType.SHEAR,
            serverPlayer.level().getRandom()
        );
    }

    private boolean isShearing(
        Shearable shearable,
        ServerPlayer player, InteractionHand hand
    ) {
        ItemStack itemStack = player.getItemInHand(hand);
        return (
            itemStack.is(Items.SHEARS) && shearable.readyForShearing()
        );
    }
}
