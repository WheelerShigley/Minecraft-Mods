package me.wheelershigley.portal_network.mixins;

import me.wheelershigley.portal_network.PortalNetwork;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(FlintAndSteelItem.class)
public class FlintAndSteelCustomDataMixin {
    @Inject(
        method = "useOnBlock",
        at = @At("HEAD"),
        cancellable = true
    )
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if( customUseOnBlock(context) ) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    @Unique
    private static boolean customUseOnBlock(ItemUsageContext context) {
        if(context.getPlayer() == null) {
            return false;
        }

        Block targetedBlock = context.getWorld().getBlockState( context.getBlockPos() ).getBlock();
        ItemStack heldItem = context.getPlayer().getStackInHand( context.getHand() );
        boolean isPartOfValidPortal = true; //TODO

        PortalNetwork.LOGGER.info( targetedBlock.getTranslationKey() );
        if(
            isPartOfValidPortal
            && context.getPlayer().getActiveItem() != null
        ) {
            if( !heldItem.getItem().equals(Items.FLINT_AND_STEEL) ) {
                PortalNetwork.LOGGER.warn(
                    "Error in \"{}\" usage by \"{}\" as {}.",
                    Items.FLINT_AND_STEEL.getTranslationKey(),
                    context.getPlayer().getName().toString(),
                    heldItem.getItem().getTranslationKey()
                );
                return false;
            }

            if( heldItem.contains(DataComponentTypes.LODESTONE_TRACKER) ) {
                boolean isValidPortalLocationAtSavedLocation = true; //TODO

                if(isValidPortalLocationAtSavedLocation) {
                    //consume saved location
                    Optional<GlobalPos> potentialPortalLocation = Optional.empty(); {
                        if( heldItem.contains(DataComponentTypes.LODESTONE_TRACKER) ) {
                            potentialPortalLocation = heldItem.get(DataComponentTypes.LODESTONE_TRACKER).target();
                        }
                    }
                    if( potentialPortalLocation.isEmpty() ) {
                        context.getPlayer().sendMessage(
                            Text.of("problem finding saved location"),
                            true
                        );
                    }
                    heldItem.set(DataComponentTypes.LODESTONE_TRACKER, null);

                    //use saved location to place custom fire
                    return attemptPlaceCustomFire(context).isAccepted();
                }

                return false;
            } else {
                //Add location-data to Flint-and-Steel
                LodestoneTrackerComponent locationData = new LodestoneTrackerComponent(
                    Optional.of(
                        GlobalPos.create(
                            context.getWorld().getRegistryKey(),
                            context.getBlockPos()
                        )
                    ),
                    true
                );
                heldItem.set(DataComponentTypes.LODESTONE_TRACKER, locationData);

                context.getPlayer().sendMessage(
                    Text.of("saved location"),
                    true
                );
                context.getPlayer().setStackInHand(context.getHand(), heldItem);
            }
            return true;
        } else {
            context.getPlayer().sendMessage(
                Text.of("§cInvalid Portal"),
                true
            );
            return false;
        }

    }

    @Unique
    private static ActionResult attemptPlaceCustomFire(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if(
            !CampfireBlock.canBeLit(blockState)
            && !CandleBlock.canBeLit(blockState)
            && !CandleCakeBlock.canBeLit(blockState)
        ) {
            BlockPos blockPos2 = blockPos.offset( context.getSide() );
            if( AbstractFireBlock.canPlaceAt( world, blockPos2, context.getHorizontalPlayerFacing() ) ) {
                world.playSound(
                    playerEntity,
                    blockPos2,
                    SoundEvents.ITEM_FLINTANDSTEEL_USE,
                    SoundCategory.BLOCKS,
                    1.0F,
                    world.getRandom().nextFloat() * 0.4F + 0.8F
                );
                BlockState blockState2 = AbstractFireBlock.getState(world, blockPos2);

                //TODO: set custom data on block, using position
                PortalNetwork.LOGGER.info("should set custom fire");

                world.setBlockState(blockPos2, blockState2, Block.NOTIFY_ALL_AND_REDRAW);
                world.emitGameEvent(playerEntity, GameEvent.BLOCK_PLACE, blockPos);
                ItemStack itemStack = context.getStack();
                if(playerEntity instanceof ServerPlayerEntity) {
                    Criteria.PLACED_BLOCK.trigger(
                        (ServerPlayerEntity)playerEntity,
                        blockPos2,
                        itemStack
                    );
                    itemStack.damage(
                        1,
                        playerEntity,
                        LivingEntity.getSlotForHand( context.getHand() )
                    );
                }

                return ActionResult.SUCCESS;
            } else {
                return ActionResult.FAIL;
            }
        } else {
            world.playSound(playerEntity, blockPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
            world.setBlockState(blockPos, blockState.with(Properties.LIT, true), Block.NOTIFY_ALL_AND_REDRAW);
            world.emitGameEvent(playerEntity, GameEvent.BLOCK_CHANGE, blockPos);
            if (playerEntity != null) {
                context.getStack().damage(1, playerEntity, LivingEntity.getSlotForHand(context.getHand()));
            }

            return ActionResult.SUCCESS;
        }
    }

}