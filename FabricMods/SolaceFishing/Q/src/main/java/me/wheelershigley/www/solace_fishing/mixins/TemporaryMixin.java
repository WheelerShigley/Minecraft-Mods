package me.wheelershigley.www.solace_fishing.mixins;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public class TemporaryMixin {
    @Inject(
        method = "tryItemClickBehaviourOverride",
        at = @At("HEAD")
    )
    private void debugItemClickOverride(
        Player player,
        ClickAction action,
        Slot slot,
        ItemStack clicked,
        ItemStack carried,
        CallbackInfoReturnable<Boolean> cir
    ) {
        System.out.println(
            "OVERRIDE DISPATCH: side=" + player.level().isClientSide()
            + " action=" + action
            + " clicked=" + clicked
            + " clickedItem=" + clicked.getItem()
            + " carried=" + carried
            + " carriedItem=" + carried.getItem()
        );
    }
}
