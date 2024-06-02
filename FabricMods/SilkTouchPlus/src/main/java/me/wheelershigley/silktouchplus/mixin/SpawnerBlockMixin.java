package me.wheelershigley.silktouchplus.mixin;

import external.kaupenjoe.ModConfigs;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnerBlock.class)
public class SpawnerBlockMixin {
    @Inject(method = "onStacksDropped", at = @At("HEAD"), cancellable = true)
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience, CallbackInfo ci) {
        if( ModConfigs.SPAWNER && 0 < EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) ) {
            ci.cancel();
        }
    }

}