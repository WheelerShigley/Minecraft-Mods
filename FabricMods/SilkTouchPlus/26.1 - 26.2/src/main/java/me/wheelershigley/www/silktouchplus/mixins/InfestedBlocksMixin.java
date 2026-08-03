package me.wheelershigley.www.silktouchplus.mixins;

import me.wheelershigley.www.silktouchplus.helpers.EnchantmentsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        BlockState state,
        final ServerLevel level, BlockPos pos,
        ItemStack tool,
        boolean dropExperience,
        CallbackInfo ci
    ) {
        super.spawnAfterBreak(state, level, pos, tool, dropExperience);
        if(
            level.getGameRules().get(GameRules.SPAWN_MOBS)
            && !EnchantmentHelper.hasTag(tool, EnchantmentTags.PREVENTS_INFESTED_SPAWNS)
            && !EnchantmentsHelper.hasSilkTouch(level, tool)
        ) {
            this.spawnInfestation(level, pos);
        }
        ci.cancel();
    }
}
