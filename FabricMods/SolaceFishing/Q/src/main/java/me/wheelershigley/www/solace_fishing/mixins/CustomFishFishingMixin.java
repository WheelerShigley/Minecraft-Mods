package me.wheelershigley.www.solace_fishing.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wheelershigley.www.solace_fishing.data.ClimateData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FishingHook.class)
public abstract class CustomFishFishingMixin {
    @Shadow
    public abstract @Nullable Player getPlayerOwner();

    @ModifyExpressionValue(
        method = "retrieve",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"
        )
    )
    public ObjectArrayList<ItemStack> test(
        ObjectArrayList<ItemStack> original
    ) {
        Player caster = this.getPlayerOwner();
        if(caster == null) {
            return original;
        }

        Level level = caster.level();
        if( !(level instanceof ServerLevel) ) {
            return original;
        }
        assert level instanceof ServerLevel;

        //get climate
        ClimateData climate = ClimateData.sample(
            (ServerLevel)level,
            caster.getOnPos()
        );

        caster.sendSystemMessage(
            Component.literal(
                climate.toString()
            )
        );

        return ObjectArrayList.of( new ItemStack(Items.STONE) );
    }
}
