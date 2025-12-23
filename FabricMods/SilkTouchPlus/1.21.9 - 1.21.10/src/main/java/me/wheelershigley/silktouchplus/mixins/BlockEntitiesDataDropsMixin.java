package me.wheelershigley.silktouchplus.mixins;

import me.wheelershigley.silktouchplus.SilkTouchPlus;
import me.wheelershigley.silktouchplus.helpers.ItemStacksHelper;
import me.wheelershigley.silktouchplus.registrations.GameRuleRegistrator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
public class BlockEntitiesDataDropsMixin {
    @Inject(
        method = "Lnet/minecraft/block/Block;getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void getDroppedStacks(
        BlockState state,
        ServerWorld world,
        BlockPos pos,
        @Nullable BlockEntity blockEntity,
        Entity entity,
        ItemStack stack,
        CallbackInfoReturnable< List<ItemStack> > cir
    ) {
        if(blockEntity != null) {
            Item itemWithBlockEntityData = getItemForModifiedBlockData(blockEntity);
            if(itemWithBlockEntityData != null) {
                ItemStack itemStackWithBlockEntityData = ItemStacksHelper.copyBlockDataToStack( blockEntity, world, pos, new ItemStack(itemWithBlockEntityData) );
                cir.setReturnValue( List.of(itemStackWithBlockEntityData) );
            }
        }
    }

    @Unique
    private static @Nullable Item getItemForModifiedBlockData(@Nullable BlockEntity blockEntity) {
        //input validation
        if(blockEntity == null) {
            return null;
        }

        GameRules gameRules = null; {
            World world = blockEntity.getWorld();
            if(world == null) {
                return null;
            }

            MinecraftServer server = blockEntity.getWorld().getServer();
            if(server == null) {
                return null;
            }

            gameRules = server.getGameRules();
        }
        //modified blocks are: Spawner, Vault, Trial_Spawner, and Suspicious Blocks
        if(
            blockEntity instanceof MobSpawnerBlockEntity
            && gameRules.getBoolean(GameRuleRegistrator.SILKTOUCH_SPAWNER)
        ) {
            return Items.SPAWNER;
        }

        if(
            blockEntity instanceof VaultBlockEntity
            && gameRules.getBoolean(GameRuleRegistrator.SILKTOUCH_VAULT)
        ) {
            return Items.VAULT;
        }

        if(
            blockEntity instanceof TrialSpawnerBlockEntity
            && gameRules.getBoolean(GameRuleRegistrator.SILKTOUCH_TRIAL_SPAWNER)
        ) {
            return Items.TRIAL_SPAWNER;
        }

        if(blockEntity instanceof BrushableBlockEntity) {
            Item item = ( (BrushableBlockEntity)blockEntity ).getItem().getItem();
            if(
                item.equals(Items.SUSPICIOUS_SAND)
                && gameRules.getBoolean(GameRuleRegistrator.SILKTOUCH_SUSPICIOUS_SAND)
            ) {
                return item;
            }
            if(
                item.equals(Items.SUSPICIOUS_GRAVEL)
                && gameRules.getBoolean(GameRuleRegistrator.SILKTOUCH_SUSPICIOUS_GRAVEL)
            ) {
                return item;
            }
        }

        return null;
    }
}
