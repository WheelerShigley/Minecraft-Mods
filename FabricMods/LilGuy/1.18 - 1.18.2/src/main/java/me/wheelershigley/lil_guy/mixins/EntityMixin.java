package me.wheelershigley.lil_guy.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow @Final private static TrackedData< Optional<Text> > CUSTOM_NAME;
    @Shadow @Final protected DataTracker dataTracker;
    @Shadow @Nullable public abstract Text getCustomName();

    @Inject(
        method = "interact",
        at = @At("TAIL"),
        cancellable = true
    )
    public void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack interactionItem = player.getStackInHand(hand);
        if(
            interactionItem.isOf(Items.NAME_TAG)
            && !interactionItem.hasCustomName()
            && !Text.EMPTY.equals( this.getCustomName() )
        ) {
            //Remove custom name
            this.dataTracker.set( CUSTOM_NAME, Optional.empty() );

            //use item
            interactionItem.decrement(1);
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
