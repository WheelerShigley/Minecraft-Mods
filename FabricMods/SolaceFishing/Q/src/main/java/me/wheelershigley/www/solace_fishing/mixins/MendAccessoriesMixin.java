package me.wheelershigley.www.solace_fishing.mixins;

import me.wheelershigley.www.solace_fishing.api.fishing.RodAccessories;
import me.wheelershigley.www.solace_fishing.api.lore.LoreRenderedRodAccessoryComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.wheelershigley.www.solace_fishing.helpers.MetaFishingHelper.isFishingRod;

@Mixin(ExperienceOrb.class)
public class MendAccessoriesMixin {
    @Inject(
        method = "repairPlayerItems",
        at = @At("RETURN"),
        cancellable = true
    )
    private static void repairAccessoriesOfNonMendingRod(
        ServerPlayer player, int amount,
        CallbackInfoReturnable<Integer> cir
    ) {
        amount = attemptRepair(player, amount);
        cir.setReturnValue(amount);
    }

    @Unique
    private static int attemptRepair(ServerPlayer player, int amount) {
        // repair accessory
        ItemStack rod = player.getMainHandItem();
        if(  !isFishingRod( rod.getItem() )  ) {
            rod = player.getOffhandItem();
        }
        if(  !isFishingRod( rod.getItem() )  ) {
            return amount;
        }

        // If the accessory consumed all XP:
        RodAccessories accessories = RodAccessories.of(rod, player.level() );
        if(accessories == null) {
            return amount;
        }
        LoreRenderedRodAccessoryComponent accessoryComponent = new LoreRenderedRodAccessoryComponent(accessories, player.level() );

        int amount_left = accessories.mend(amount, player.level() );
        if(amount_left != amount) {
            accessoryComponent.set(rod);
        }
        return amount_left;
    }
}
