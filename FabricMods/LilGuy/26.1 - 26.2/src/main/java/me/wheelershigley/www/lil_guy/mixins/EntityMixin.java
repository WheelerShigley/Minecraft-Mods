package me.wheelershigley.www.lil_guy.mixins;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(Entity.class)
public abstract class EntityMixin implements SyncedDataHolder {

    @Shadow @Final private static EntityDataAccessor< Optional<Component> > DATA_CUSTOM_NAME;
    @Shadow @Final protected SynchedEntityData entityData;
    @Shadow @Nullable public abstract Component getCustomName();

    @Inject(
        method = "interact",
        at = @At("HEAD"),
        cancellable = true
    )
    public void interact(
        Player player, InteractionHand hand,
        Vec3 location,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        ItemStack interactionItem = player.getItemInHand(hand);
        Component customName = this.getCustomName();
        if(
            interactionItem.is(Items.NAME_TAG)
            && !interactionItem.has(DataComponents.CUSTOM_NAME)
            && customName != null && !Component.empty().equals(customName)
        ) {
            //Remove custom name
            this.entityData.set( DATA_CUSTOM_NAME, Optional.empty() );

            //use item
            interactionItem.shrink(1);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
