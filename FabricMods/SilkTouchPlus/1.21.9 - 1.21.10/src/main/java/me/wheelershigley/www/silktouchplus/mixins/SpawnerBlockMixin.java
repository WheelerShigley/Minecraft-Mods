package me.wheelershigley.www.silktouchplus.mixins;

import me.wheelershigley.www.silktouchplus.helpers.EnchantmentsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.wheelershigley.www.silktouchplus.registrations.GameRuleRegistrator.SILKTOUCH_SPAWNER;

@Mixin(SpawnerBlock.class)
public abstract class SpawnerBlockMixin extends BaseEntityBlock {
    protected SpawnerBlockMixin(Properties properties) {
        super(properties);
    }

    /**
     * @author wheelershigley
     * @reason Since spawners drop with SilkTouch, it should not yield Exp when dropped.
     */
    @Inject(
        method = "spawnAfterBreak",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void spawnAfterBreak(
        BlockState blockState,
        ServerLevel serverLevel, BlockPos blockPos,
        ItemStack tool, boolean bl,
        CallbackInfo ci
    ) {
        super.spawnAfterBreak(blockState, serverLevel, blockPos, tool, bl);

        /* Exp. should not drop if the spawner is silk-touched. */ {
            if(
                serverLevel.getGameRules().getBoolean(SILKTOUCH_SPAWNER)
                && EnchantmentsHelper.hasSilkTouch(serverLevel, tool)
                && tool.is(ItemTags.PICKAXES)
            ) {
                bl = false;
            }
        }
        /* Vanilla Implementation */ {
            if(bl) {
                int i = 15 + serverLevel.random.nextInt(15) + serverLevel.random.nextInt(15);
                this.popExperience(serverLevel, blockPos, i);
            }
        }
        ci.cancel();
    }
}
