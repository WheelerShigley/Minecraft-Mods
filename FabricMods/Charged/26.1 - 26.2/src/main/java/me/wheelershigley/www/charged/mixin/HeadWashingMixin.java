package me.wheelershigley.www.charged.mixin;

import net.minecraft.core.cauldron.CauldronInteractions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LayeredCauldronBlock;

import static me.wheelershigley.www.charged.gamerules.GameRuleRegistrar.ENABLE_PLAYER_HEAD_TEXTURE_WASHING;
import static net.minecraft.core.cauldron.CauldronInteractions.WATER;

@Mixin(CauldronInteractions.class)
public class HeadWashingMixin {

    @Inject(
        method = "bootStrap",
        at = @At("TAIL")
    )
    private static void registerBehavior(CallbackInfo ci) {
        WATER.put(
            Items.PLAYER_HEAD,
            (state, world, pos, player, hand, stack) -> {
                if( !(world instanceof ServerLevel) ) {
                    return InteractionResult.PASS;
                }
                boolean isTextureWashingEnabled = ( (ServerLevel)world ).getGameRules().get(ENABLE_PLAYER_HEAD_TEXTURE_WASHING);

                world.getServer();
                if(!isTextureWashingEnabled) {
                    return InteractionResult.PASS;
                }

                if( state.getValue(LayeredCauldronBlock.LEVEL) <= 0 ) {
                    return InteractionResult.TRY_WITH_EMPTY_HAND;
                } else {
                    if( stack.has(DataComponents.PROFILE) ) {
                        player.setItemInHand(
                            hand,
                            ItemUtils.createFilledResult( stack, player, new ItemStack(Items.PLAYER_HEAD) )
                        );
                        player.awardStat(Stats.USE_CAULDRON);
                        player.awardStat(  Stats.ITEM_USED.get( stack.getItem() )  );
                        LayeredCauldronBlock.lowerFillLevel(state, world, pos);
                        world.playSound(
                            (Entity)null,
                            pos,
                            SoundEvents.WATER_AMBIENT,
                            SoundSource.BLOCKS,
                            1.0F,
                            1.0F
                        );

                        return InteractionResult.SUCCESS_SERVER;
                    }
                }
                return InteractionResult.PASS;
            }
        );
    }
}
