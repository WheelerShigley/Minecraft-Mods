package me.wheelershigley.www.silktouchplus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static me.wheelershigley.www.silktouchplus.registrations.GameRuleRegistrator.*;

@Mixin(Block.class)
public class BlockEntitiesDataDropsMixin {
    @Inject(
        method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)Ljava/util/List;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void getDroppedStacks(
        BlockState state,
        final ServerLevel level, BlockPos pos,
        BlockEntity blockEntity,
        CallbackInfoReturnable< List<ItemStack> > cir
    ) {
        if(blockEntity != null) {
            Item itemWithBlockEntityData = getItemForModifiedBlockData(blockEntity);
            if(itemWithBlockEntityData != null) {
                cir.setReturnValue(
                    List.of( new ItemStack(itemWithBlockEntityData) )
                );
            }
        }
    }

    @Unique
    private static @Nullable Item getItemForModifiedBlockData(@Nullable BlockEntity blockEntity) {
        //input validation
        if(blockEntity == null) {
            return null;
        }

        ServerLevel level = (ServerLevel)blockEntity.getLevel();
        if(level == null) {
            return null;
        }
        GameRules gameRules = level.getGameRules();

        //modified blocks are: Spawner, Vault, Trial_Spawner, and Suspicious Blocks
        if(
            blockEntity instanceof SpawnerBlockEntity
            && gameRules.get(SILKTOUCH_SPAWNER)
        ) {
            return Items.SPAWNER;
        }

        if(
            blockEntity instanceof VaultBlockEntity
            && gameRules.get(SILKTOUCH_VAULT)
        ) {
            return Items.VAULT;
        }

        if(
            blockEntity instanceof TrialSpawnerBlockEntity
            && gameRules.get(SILKTOUCH_TRIAL_SPAWNER)
        ) {
            return Items.TRIAL_SPAWNER;
        }

        if(blockEntity instanceof BrushableBlockEntity) {
            Item item = ( (BrushableBlockEntity)blockEntity ).getItem().getItem();
            if(
                item.equals(Items.SUSPICIOUS_SAND)
                && gameRules.get(SILKTOUCH_SUSPICIOUS_SAND)
            ) {
                return item;
            }
            if(
                item.equals(Items.SUSPICIOUS_GRAVEL)
                && gameRules.get(SILKTOUCH_SUSPICIOUS_GRAVEL)
            ) {
                return item;
            }
        }

        return null;
    }
}
