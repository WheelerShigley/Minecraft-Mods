package me.wheelershigley.www.solace_crates.blocks;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import me.wheelershigley.www.solace_crates.SolaceCrates;
import me.wheelershigley.www.solace_crates.crate.CrateDefinition;
import me.wheelershigley.www.solace_crates.items.ItemsHelper;
import me.wheelershigley.www.solace_crates.loot.ItemStackListOddsTable;
import me.wheelershigley.www.solace_crates.loot.LootHelper;
import me.wheelershigley.www.solace_crates.loot.LootTableEvaluator;
import me.wheelershigley.www.solace_crates.menus.ProbabilitiesMenu;
import me.wheelershigley.www.solace_crates.permissions.PermissionsHelper;
import me.wheelershigley.www.solace_crates.registry.CrateRegistry;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.wheelershigley.www.solace_crates.SolaceCrates.getTranslatedSolaceCrateComponent;

public class CrateBlock extends BaseEntityBlock implements PolymerTexturedBlock {
    public static final String NAME = "crate";
    public static final BlockBehaviour.Properties PROPERTIES;
    private final BlockState STATE;

    public CrateBlock(Properties properties) {
        super(PROPERTIES);

        STATE = PolymerBlockResourceUtils.requestBlock(
            BlockModelType.FULL_BLOCK,
            PolymerBlockModel.of(
                SolaceCrates.id("block/" + NAME)
            )
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CrateBlock::new);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, @Nullable PacketContext context) {
        return this.STATE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(
        @NonNull BlockPos position,
        @NonNull BlockState blockState
    ) {
        return new CrateBlockEntity(position, blockState);
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

        if( serverPlayer.isCrouching() ) {
            LootTable table = crateDefinition.getLootTable( serverPlayer.level().getServer() );
            if(table == null) {
                serverPlayer.sendSystemMessage(
                    getTranslatedSolaceCrateComponent("solace_crates.messages.loottable_missing")
                );
                return InteractionResult.FAIL;
            }
            LootContext context = LootHelper.createLootContext(serverPlayer);
            ItemStackListOddsTable odds = LootTableEvaluator.evaluate(table, context);

            ProbabilitiesMenu menu = new ProbabilitiesMenu(serverPlayer, odds, crateDefinition, null) {
                @Override
                public String getTranslationKey() {
                    return crateBlockEntity.getIdentifiedTranslationKey();
                }
            };

            menu.open();
            return InteractionResult.SUCCESS;
        }

        final Item STANDARD_KEY = ItemsHelper.getItem( crateDefinition.STANDARD_KEY );
        if( itemStack.getItem().equals(STANDARD_KEY) ) {
            player.sendSystemMessage(
                Component.literal("TODO: standard key usage")
            );
            return InteractionResult.SUCCESS_SERVER;
        }

        final Item PREMIUM_KEY  = ItemsHelper.getItem( crateDefinition.PREMIUM_KEY  );
        if( itemStack.getItem().equals(PREMIUM_KEY) ) {
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

    static {
        PROPERTIES = BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(-1.0F, 3600000.0F)
            .noLootTable()
            .isValidSpawn(Blocks::never)
            .sound(SoundType.VAULT)
        ;
    }
}
