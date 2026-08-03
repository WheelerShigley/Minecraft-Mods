package me.wheelershigley.www.silktouchplus.mixins;

import me.wheelershigley.www.silktouchplus.helpers.EnchantmentsHelper;
import net.fabricmc.fabric.api.block.v1.FabricBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static me.wheelershigley.www.silktouchplus.registrations.GameRuleRegistrator.*;

@Mixin(Block.class)
public class DropBlockItemsMixin implements FabricBlock {
    @Inject(
        method = "playerDestroy",
        at = @At("TAIL")
    )
    public void playerDestroy(
        Level level, Player player,
        BlockPos blockPos, BlockState blockState, BlockEntity blockEntity,
        ItemStack itemStack,
        CallbackInfo ci
    ) {
        ServerLevel serverLevel = null;
        if(level instanceof ServerLevel) {
            serverLevel = (ServerLevel)level;
        }
        if(serverLevel == null) {
            return;
        }

        boolean brokenWithoutSilkTouch = !EnchantmentsHelper.hasSilkTouch(
            serverLevel,
            player.getMainHandItem()
        );
        if(brokenWithoutSilkTouch) {
            return;
        }

        ItemStack tool = player.getMainHandItem();

        //simple blocks
        if( attemptSimpleDrops(serverLevel, blockPos, blockState, tool) ) {
            return;
        }
        //block-entities
        if( blockEntityShouldDrop(serverLevel, blockEntity, blockState, tool) ) {
            dropBlockEntityItem(serverLevel, blockPos, blockEntity);
        }
        //special case(s)
        if( blockState.getBlock() == Blocks.TRIAL_SPAWNER
            && serverLevel.getGameRules().getBoolean(SILKTOUCH_TRIAL_SPAWNER)
            && tool.is(ItemTags.PICKAXES)
        ) {
            dropTrialSpawnerItem(serverLevel, blockPos, blockEntity);
        }
    }

    @Unique
    private boolean blockEntityShouldDrop(
        ServerLevel level,
        BlockEntity blockEntity, BlockState blockState,
        ItemStack tool
    ) {
        Block block = blockState.getBlock();

        boolean allowedByPickaxe = tool.is(ItemTags.PICKAXES) && (
            (
            blockEntity instanceof SpawnerBlockEntity
            && level.getGameRules().getBoolean(SILKTOUCH_SPAWNER)
            ) || (
                blockEntity instanceof VaultBlockEntity
                && level.getGameRules().getBoolean(SILKTOUCH_VAULT)
            )
        );

        boolean allowedWithoutPickaxe = (
            block == Blocks.SUSPICIOUS_SAND
            && level.getGameRules().getBoolean(SILKTOUCH_SUSPICIOUS_SAND)
        ) || (
            block == Blocks.SUSPICIOUS_GRAVEL
            && level.getGameRules().getBoolean(SILKTOUCH_SUSPICIOUS_GRAVEL)
        );

        return allowedByPickaxe || allowedWithoutPickaxe;
    }

    @Unique
    private boolean attemptSimpleDrops(
        ServerLevel serverLevel,
        BlockPos blockPos, BlockState blockState,
        ItemStack tool
    ) {
        Block block = blockState.getBlock();
        GameRules gameRules = serverLevel.getGameRules();

        if( block == Blocks.DIRT_PATH
            && gameRules.getBoolean(SILKTOUCH_DIRT_PATH)
        ) {
            dropSimpleItem(serverLevel, blockPos, Items.DIRT_PATH);
            return true;
        }
        if( block == Blocks.FARMLAND
            && gameRules.getBoolean(SILKTOUCH_FARMLAND)
        ) {
            dropSimpleItem(serverLevel, blockPos, Items.FARMLAND);
            return true;
        }

        boolean isPickaxe = tool.is(ItemTags.PICKAXES);
        if( block == Blocks.BUDDING_AMETHYST
            && gameRules.getBoolean(SILKTOUCH_BUDDING_AMETHYST)
            && isPickaxe
        ) {
            dropSimpleItem(serverLevel, blockPos, Items.BUDDING_AMETHYST);
            return true;
        }
        if( block == Blocks.REINFORCED_DEEPSLATE
            && gameRules.getBoolean(SILKTOUCH_REINFORCED_DEEPSLATE)
            && isPickaxe
        ) {
            dropSimpleItem(serverLevel, blockPos, Items.REINFORCED_DEEPSLATE);
            return true;
        }

        return false;
    }

    @Unique
    private void dropSimpleItem(ServerLevel level, BlockPos pos, Item item) {
        ItemEntity itemEntity = new ItemEntity(
            level,
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5,
            new ItemStack(item)
        );

        level.addFreshEntity(itemEntity);
    }

    @Unique
    private void dropBlockEntityItem(ServerLevel level, BlockPos pos, BlockEntity blockEntity) {
        ItemEntity itemEntity = new ItemEntity(
            level,
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5,
            getBlockEntityItemWithData(level, pos, blockEntity)
        );

        level.addFreshEntity(itemEntity);
    }

    @Unique
    private ItemStack getBlockEntityItemWithData(
        ServerLevel level, BlockPos pos,
        BlockEntity blockEntity
    ) {
        BlockState state = blockEntity.getBlockState();
        ItemStack stack = state.getBlock().getCloneItemStack(level, pos, state, true);
        CompoundTag tag = blockEntity.saveCustomOnly( level.registryAccess() ); {
            Optional<CompoundTag> sharedData = tag.getCompound("shared_data");
            sharedData.ifPresent(
                (compoundTag) -> {
                    compoundTag.remove("display_item");
                }
            );

            Optional<CompoundTag> serverData = tag.getCompound("server_data");
            serverData.ifPresent(
                (compoundTag) -> {
                    compoundTag.remove("state_updating_resumes_at");
                }
            );
        }
        stack.set(
            DataComponents.BLOCK_ENTITY_DATA,
            TypedEntityData.of(
                blockEntity.getType(),
                tag
            )
        );
        return stack;
    }

    @Unique
    private void dropTrialSpawnerItem(ServerLevel level, BlockPos pos, BlockEntity blockEntity) {
        BlockState state = blockEntity.getBlockState();
        ItemStack stack = state.getBlock().getCloneItemStack(level, pos, state, true);
        CompoundTag tag = blockEntity.saveCustomOnly( level.registryAccess() );

        /* Removes Unnecessary Data*/ {
            tag.remove("x");
            tag.remove("y");
            tag.remove("z");
            tag.remove("components");

            Optional<CompoundTag> sharedData = tag.getCompound("shared_data");
            sharedData.ifPresent(
                (compoundTag) -> {
                    compoundTag.remove("display_item");
                }
            );
        }

        stack.set(
            DataComponents.BLOCK_ENTITY_DATA,
            TypedEntityData.of(
                blockEntity.getType(),
                tag
            )
        );

        ItemEntity itemEntity = new ItemEntity(
            level,
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5,
            stack
        );

        level.addFreshEntity(itemEntity);
    }
}
