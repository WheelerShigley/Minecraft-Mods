package me.wheelershigley.www.solace_crates.blocks;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import me.wheelershigley.www.solace_crates.SolaceCrates;
import me.wheelershigley.www.solace_crates.crate.CrateDefinition;
import me.wheelershigley.www.solace_crates.crate.CrateElementHolder;
import me.wheelershigley.www.solace_crates.items.ItemsHelper;
import me.wheelershigley.www.solace_crates.loot.ItemStackOddsTables;
import me.wheelershigley.www.solace_crates.loot.LootHelper;
import me.wheelershigley.www.solace_crates.loot.LootTableEvaluator;
import me.wheelershigley.www.solace_crates.menus.ChoiceMenu;
import me.wheelershigley.www.solace_crates.menus.ProbabilitiesMenu;
import me.wheelershigley.www.solace_crates.mixin.LootTableAccessor;
import me.wheelershigley.www.solace_crates.permissions.PermissionsHelper;
import me.wheelershigley.www.solace_crates.registry.CrateRegistry;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.wheelershigley.www.solace_crates.SolaceCrates.getTranslatedSolaceCrateComponent;

public class CrateBlock extends BaseEntityBlock implements PolymerTexturedBlock, BlockWithElementHolder {
    public static final Identifier DEFAULT_BLOCK_IDENTIFIER = SolaceCrates.id("crate");
    public static final BlockBehaviour.Properties PROPERTIES;
    private long lastInsertFailTimestamp;

    public CrateBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CrateBlock::new);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(
        ServerLevel world,
        BlockPos pos,
        BlockState state
    ) {
        return new CrateElementHolder(world, pos);
    }

    @Override
    public boolean tickElementHolder(ServerLevel world, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(
        @NonNull BlockPos position,
        @NonNull BlockState blockState
    ) {
        return new CrateBlockEntity(position, blockState);
    }

    void setLastInsertFailTimestamp(final long lastInsertFailTimestamp) {
        this.lastInsertFailTimestamp = lastInsertFailTimestamp;
    }

    long getLastInsertFailTimestamp() {
        return this.lastInsertFailTimestamp;
    }

    @Override
    protected @NonNull InteractionResult useItemOn(
        final @NonNull ItemStack itemStack,
        final @NonNull BlockState state,
        final @NonNull Level level,
        final @NonNull BlockPos pos,
        final @NonNull Player player,
        final @NonNull InteractionHand hand,
        final @NonNull BlockHitResult hitResult
    ) {
        if( !(player instanceof ServerPlayer serverPlayer) ) {
            return InteractionResult.PASS;
        }

        CrateBlockEntity crateBlockEntity = (CrateBlockEntity)level.getBlockEntity(pos);
        if(crateBlockEntity == null) {
            return InteractionResult.FAIL;
        }

        if( crateBlockEntity.getCrateIdentifier().equals(CrateBlockEntity.DEFAULT_IDENTIFIER) ) {
            boolean hasPermission = PermissionsHelper.hasCommandPermission(serverPlayer);
            serverPlayer.sendSystemMessage(
                getTranslatedSolaceCrateComponent(
                    "solace_crates.message.crate_unset." + (hasPermission ? "allowed" : "disallowed")
                )
            );
            return InteractionResult.SUCCESS_SERVER;
        }

        CrateDefinition crateDefinition = CrateRegistry.get( crateBlockEntity.getCrateIdentifier() );
        if(crateDefinition == null) {
            serverPlayer.sendSystemMessage(
                getTranslatedSolaceCrateComponent(
                    "solace_crates.message.definition_missing"
                )
            );
            return InteractionResult.FAIL;
        }

        LootTable table = crateDefinition.getLootTable();
        if(table == null) {
            serverPlayer.sendSystemMessage(
                getTranslatedSolaceCrateComponent("solace_crates.messages.loottable_missing")
            );
            return InteractionResult.FAIL;
        }
        // TODO: seeding
        LootContext context = LootHelper.createLootContext(serverPlayer, 0);

        if( serverPlayer.isCrouching() ) {
            ItemStackOddsTables odds = LootTableEvaluator.evaluate(table, context);
            List<LootPool> pools = ( (LootTableAccessor)table ).solace_crates$getPools();

            ProbabilitiesMenu menu = new ProbabilitiesMenu.Builder()
                .withLootPools(pools)
                .ofCrateBlockEntity(crateBlockEntity)
                .build(
                    serverPlayer, odds, context, crateDefinition
                )
            ;
            menu.open();
            return InteractionResult.SUCCESS;
        }

        final boolean itemAccepted = CrateBlock.Server.tryInsertKey(
            serverPlayer,
            serverPlayer.getItemInHand( serverPlayer.getUsedItemHand() ),
            serverPlayer.level(),
            crateBlockEntity.getBlockPos(),
            this,
            crateDefinition,
            table,
            context
        );
        if(itemAccepted) {
            return InteractionResult.SUCCESS_SERVER;
        }

        final Item STANDARD_KEY = ItemsHelper.getItem(crateDefinition.STANDARD_KEY());
        final Item PREMIUM_KEY  = ItemsHelper.getItem(crateDefinition.PREMIUM_KEY());
        if( itemStack.getItem().equals(PREMIUM_KEY) ) {
            ItemStackOddsTables odds = LootTableEvaluator.evaluate(table, context);
            ChoiceMenu menu = new ChoiceMenu(
                serverPlayer, crateDefinition, context, odds.getAllEntries(context), null
            );
            menu.open();

            player.sendSystemMessage(
                Component.literal("TODO: premium key usage")
            );
            return InteractionResult.SUCCESS_SERVER;
        }

        Component missingKeysMessage; {
            List<Component> translations = new ArrayList<>();
            if(STANDARD_KEY != null) {
                translations.add(
                    Component.translatable( STANDARD_KEY.getDescriptionId() )
                );
            }
            if(PREMIUM_KEY  != null) {
                translations.add(
                    Component.translatable(  PREMIUM_KEY.getDescriptionId() )
                );
            }

            String translationIndex = switch( translations.size() ) {
                case 1 -> "single";
                case 2 -> "double";
                default -> "none";
            };
            missingKeysMessage = Component.translatable(
                "solace_crates.message.key_missing." + translationIndex,
                (Object[])translations.toArray( new Component[0] )
            );
        }
        player.sendSystemMessage(
            getTranslatedSolaceCrateComponent(missingKeysMessage)
        );
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, @Nullable PacketContext context) {
        return Blocks.BARRIER.defaultBlockState();
    }

    // inspired by VaultBlock.Server
    private static class Server {
        public static boolean tryInsertKey(
            final ServerPlayer serverPlayer,
            final ItemStack stackToInsert,
            final ServerLevel serverLevel,
            final BlockPos pos,
            final CrateBlock crateBlock,
            final CrateDefinition crateDefinition,
            final LootTable lootTable,
            final LootContext lootContext
        ) {
            if( !isValidToInsert(crateDefinition, stackToInsert.getItem() ) ) {
                playInsertFailSound(serverLevel, crateBlock, pos, SoundEvents.VAULT_INSERT_ITEM_FAIL);
                return false;
            }

            List<ItemStack> stacks = new ArrayList<>();
            lootTable.getRandomItemsRaw(lootContext, stacks::add);
            if( stacks.isEmpty() ) {
                return false;
            }

            serverPlayer.awardStat(
                Stats.ITEM_USED.get( stackToInsert.getItem() )
            );
            stackToInsert.consume(1, serverPlayer);
            ItemsHelper.giveItemsToPlayer(serverPlayer, stacks);
            return true;
        }

        private static boolean isValidToInsert(
            CrateDefinition crateDefinition,
            Item item
        ) {
            if(crateDefinition == null) {
                return false;
            }

            Identifier itemIdentifier = ItemsHelper.getIdentifier(item);
            return itemIdentifier.equals(crateDefinition.STANDARD_KEY());
        }

        private static void playInsertFailSound(
            final ServerLevel serverLevel,
            final CrateBlock crateBlock,
            final BlockPos pos,
            final SoundEvent sound
        ) {
            if( crateBlock.getLastInsertFailTimestamp() + 15L <= serverLevel.getGameTime() ) {
                serverLevel.playSound(null, pos, sound, SoundSource.BLOCKS);
                crateBlock.setLastInsertFailTimestamp( serverLevel.getGameTime() );
            }
        }
    }

    static {
        PROPERTIES = BlockBehaviour.Properties.of()
            .noOcclusion()
            .mapColor(MapColor.STONE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(-1.0F, 3600000.0F)
            .noLootTable()
            .isValidSpawn(Blocks::never)
            .sound(SoundType.VAULT)
        ;
    }
}
