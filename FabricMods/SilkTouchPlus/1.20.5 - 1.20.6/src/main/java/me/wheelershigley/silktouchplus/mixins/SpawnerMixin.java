package me.wheelershigley.silktouchplus.mixins;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(SpawnerBlock.class)
public abstract class SpawnerMixin extends BlockWithEntity  {
    protected SpawnerMixin(Settings settings) {
        super(settings);
    }

    @Final
    @Shadow
    public static MapCodec<SpawnerBlock> CODEC;

    public MapCodec<SpawnerBlock> getCodec() {
        return CODEC;
    }

    /**
     * @author wheelershigley
     * @reason Since spawners drop with SilkTouch, it should not yield Exp when dropped.
     */
    @Inject(method = "Lnet/minecraft/block/SpawnerBlock;onStacksDropped(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;Z)V", at = @At("HEAD"))
    protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience, CallbackInfo ci) {
        /* Exp. should not drop if the spawner is silk-touched. */ {
            Set<RegistryEntry<Enchantment>> enchants = EnchantmentHelper.getEnchantments(tool).getEnchantments();
            boolean includes_silktouch = false;
            for(RegistryEntry<Enchantment> enchant : enchants) {
                if(enchant.value() == Enchantments.SILK_TOUCH) {
                    includes_silktouch = true;
                }
            }

            if(/*dropExperience && */includes_silktouch) {
                dropExperience = false;
            }
        }

        /* Vanilla Implementation */ /*{
            super.onStacksDropped(state, world, pos, tool, dropExperience);
            if(dropExperience) {
                int i = 15 + world.random.nextInt(15) + world.random.nextInt(15);
                this.dropExperience(world, pos, i);
            }
        }*/
    }
}
