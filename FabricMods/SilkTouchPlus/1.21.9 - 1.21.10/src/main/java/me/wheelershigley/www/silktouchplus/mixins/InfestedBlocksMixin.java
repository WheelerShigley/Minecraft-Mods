package me.wheelershigley.www.silktouchplus.mixins;

import me.wheelershigley.www.silktouchplus.helpers.EnchantmentsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.wheelershigley.www.silktouchplus.registrations.GameRuleRegistrator.SILKTOUCH_INFESTED_BLOCKS;

@Mixin(InfestedBlock.class)
public class InfestedBlocksMixin extends Block {
    public InfestedBlocksMixin(Properties properties) {
        super(properties);
    }

    @Shadow
    private void spawnInfestation(ServerLevel serverLevel, BlockPos blockPos) {}

    @Inject(
        method = "spawnAfterBreak",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void spawnAfterBreak(
        BlockState blockState,
        ServerLevel serverLevel, BlockPos blockPos,
        ItemStack itemStack,
        boolean bl,
        CallbackInfo ci
    ) {
        super.spawnAfterBreak(blockState, serverLevel, blockPos, itemStack, bl);
        if(
            serverLevel.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)
            && !EnchantmentHelper.hasTag(itemStack, EnchantmentTags.PREVENTS_INFESTED_SPAWNS)
            && !EnchantmentsHelper.hasSilkTouch(serverLevel, itemStack)
        ) {
            this.spawnInfestation(serverLevel, blockPos);
        }
        ci.cancel();
    }
}
