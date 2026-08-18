package me.wheelershigley.www.solace_fishing.mixins;

import me.wheelershigley.www.solace_fishing.data.AccessorizedFishingHook;
import me.wheelershigley.www.solace_fishing.data.RodAccessories;
import me.wheelershigley.www.solace_fishing.data.lore.LoreRenderedRodAccessoryComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHook.class)
public class AccessoriesWithBobberMixin implements AccessorizedFishingHook {
    @Unique
    private RodAccessories accessories = new RodAccessories();

    @Override
    public RodAccessories solace_fishing$getAccessories() {
        return this.accessories;
    }

    @Inject(
        method = "<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V",
        at = @At("TAIL")
    )
    public void associateAccessories(
        Player player, Level level,
        int luck, int lureSpeed,
        CallbackInfo ci
    ) {
        ItemStack rod = player.getMainHandItem();
        this.accessories = LoreRenderedRodAccessoryComponent.get(rod, level);
    }
}
