package me.wheelershigley.charged.mixins;

import me.wheelershigley.charged.Charged;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static net.minecraft.block.cauldron.CauldronBehavior.WATER_CAULDRON_BEHAVIOR;

@Mixin(CauldronBehavior.class)
public interface HeadWashingMixin {
    @Shadow
    static void registerBucketBehavior(Map<Item, CauldronBehavior> behavior) {}

    @Inject(
        method = "registerBehavior",
        at = @At("TAIL")
    )
    private static void registerBehavior(CallbackInfo ci) {
        Map<Item, CauldronBehavior> map = WATER_CAULDRON_BEHAVIOR.map();
        map.put(
                Items.PLAYER_HEAD,
                (state, world, pos, player, hand, stack) -> {
//                    if(Charged.PlayerHeadTextureWashing) {
//                        return ActionResult.SUCCESS_SERVER;
//                    }

                    if(state.get(LeveledCauldronBlock.LEVEL) <= 0) {
                        return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
                    } else {
                        if( stack.contains(DataComponentTypes.PROFILE) && !world.isClient ) {
                            player.setStackInHand(
                                hand,
                                ItemUsage.exchangeStack( stack, player, new ItemStack(Items.PLAYER_HEAD) )
                            );
                            player.incrementStat(Stats.USE_CAULDRON);
                            player.incrementStat(  Stats.USED.getOrCreateStat( stack.getItem() )  );
                            LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                            world.playSound(
                                (Entity)null,
                                pos,
                                SoundEvents.BLOCK_WATER_AMBIENT,
                                SoundCategory.BLOCKS,
                                1.0F,
                                1.0F
                            );

                            return ActionResult.SUCCESS_SERVER;
                        }
                    }
                    return ActionResult.PASS;
                }
        );
        registerBucketBehavior(map);
    }
}
