package me.wheelershigley.podzol_litter.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(HoeItem.class)
public class PodzolMixin {
    @Unique
    private static final BlockState RESULT = Blocks.DIRT.getDefaultState();

    //Mostly from HoeItem::createTillAndDropAction
    @Unique
    private static Consumer<ItemUsageContext> createTillAndDropActionUpToCount(BlockState result, ItemConvertible droppedItem, Random random, int max_count) {
        return (context) -> {
            context.getWorld().setBlockState(
                context.getBlockPos(),
                result,
                11
            );
            context.getWorld().emitGameEvent(
                GameEvent.BLOCK_CHANGE,
                context.getBlockPos(),
                GameEvent.Emitter.of(context.getPlayer(), result)
            );

            ItemStack droppedLeaves = new ItemStack(droppedItem);
            droppedLeaves.setCount( 1+random.nextInt(max_count) );
            Block.dropStack(
                context.getWorld(),
                context.getBlockPos(),
                context.getSide(),
                droppedLeaves
            );
        };
    }

    @Inject(
        method = "useOnBlock",
        at = @At("HEAD"),
        cancellable = true
    )
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        Block block = world.getBlockState(blockPos).getBlock();
        if( block.equals(Blocks.PODZOL) ) {
            PlayerEntity playerEntity = context.getPlayer();

            if(!world.isClient) {

                createTillAndDropActionUpToCount(
                    RESULT,
                    Items.LEAF_LITTER,
                    world.random,
                    4
                ).accept(context);

                if (playerEntity != null) {
                    context.getStack().damage(
                        1,
                        playerEntity,
                        LivingEntity.getSlotForHand( context.getHand() )
                    );

                    world.playSound(
                        playerEntity,
                        blockPos,
                        SoundEvents.ITEM_HOE_TILL,
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                    );
                }
            }

            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
