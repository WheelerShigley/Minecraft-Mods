package me.wheelershigley.www.silktouchplus.mixins;

import me.wheelershigley.www.silktouchplus.helpers.ItemStacksHelper;
import me.wheelershigley.www.silktouchplus.registrations.GameRuleRegistrator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
        method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)Ljava/util/List;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void getDroppedStacks(
        BlockState blockState,
        ServerLevel serverLevel, BlockPos blockPos,
        BlockEntity blockEntity,
        CallbackInfoReturnable< List<ItemStack> > cir
    ) {
        if(blockEntity != null) {
            Item itemWithBlockEntityData = getItemForModifiedBlockData(blockEntity);
            if(itemWithBlockEntityData != null) {
                ItemStack itemStackWithBlockEntityData = ItemStacksHelper.copyBlockDataToStack(
                    blockEntity, serverLevel, blockPos,
                    new ItemStack(itemWithBlockEntityData)
                );
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
            ServerLevel level = (ServerLevel)blockEntity.getLevel();
            if(level == null) {
                return null;
            }

            MinecraftServer server = blockEntity.getLevel().getServer();
            if(server == null) {
                return null;
            }

            gameRules = server.getGameRules();
        }
        //modified blocks are: Spawner, Vault, Trial_Spawner, and Suspicious Blocks
        if(
            blockEntity instanceof SpawnerBlockEntity
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
