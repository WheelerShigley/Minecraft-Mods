package me.wheelershigley.silktouchplus.mixins;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.enchantment.Enchantment;
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
public class SpawnerMixin {

    @Final
    @Shadow
    public static MapCodec<SpawnerBlock> CODEC;
    public MapCodec<SpawnerBlock> getCodec() {
        return CODEC;
    }
    /**
     * @wheelershigley
     * @ExpShouldNotDropWithSilkTouch
     */
    @Inject(method="onStacksDropped", at=@At("HEAD"))
    protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience, CallbackInfo ci) {
        //TODO fix that exp is still dropping

        /* Exp. should not drop if the spawner is silk-touched. */ {
            Set<RegistryEntry<Enchantment>> enchants = tool.getEnchantments().getEnchantments();
            boolean includes_silktouch = false;
            for(RegistryEntry<Enchantment> enchant : enchants) {
                if(enchant.value() == Enchantments.SILK_TOUCH) {
                    includes_silktouch = true;
                }
            }

            if(dropExperience && includes_silktouch) {
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

        //return;
    }
}
