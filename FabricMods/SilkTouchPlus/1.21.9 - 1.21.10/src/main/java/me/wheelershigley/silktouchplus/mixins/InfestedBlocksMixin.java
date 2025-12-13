package me.wheelershigley.silktouchplus.mixins;

import me.wheelershigley.silktouchplus.helpers.EnchantmentsHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InfestedBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.wheelershigley.silktouchplus.registrations.GameRuleRegistrator.SILKTOUCH_INFESTED_BLOCKS;

@Mixin(InfestedBlock.class)
public class InfestedBlocksMixin extends Block {
    public InfestedBlocksMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    private void spawnSilverfish(ServerWorld world, BlockPos pos) {}

    @Inject(
        method = "onStacksDropped",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void onStacksDropped(
        BlockState state,
        ServerWorld world,
        BlockPos pos,
        ItemStack tool,
        boolean dropExperience,
        CallbackInfo ci
    ) {
        super.onStacksDropped(state, world, pos, tool, dropExperience);
        ItemEnchantmentsComponent enchantmentsComponent = tool.get(DataComponentTypes.ENCHANTMENTS);
        if(
            world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)
            && !EnchantmentHelper.hasAnyEnchantmentsIn(tool, EnchantmentTags.PREVENTS_INFESTED_SPAWNS)
            && !(
                world.getGameRules().getBoolean(SILKTOUCH_INFESTED_BLOCKS)
                && enchantmentsComponent != null && !enchantmentsComponent.isEmpty()
                && EnchantmentsHelper.includesEnchantment(
                    enchantmentsComponent.getEnchantments(),
                    Enchantments.SILK_TOUCH
                )
            )
        ) {
            this.spawnSilverfish(world, pos);
        }
        ci.cancel();
    }
}
