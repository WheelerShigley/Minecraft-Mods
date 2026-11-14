package me.wheelershigley.www.solace_crates.blocks;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import me.wheelershigley.www.solace_crates.SolaceCrates;
import me.wheelershigley.www.solace_crates.menus.ProbabilitiesMenu;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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

        if( player.isCrouching() ) {
            CrateBlockEntity crateBlockEntity = (CrateBlockEntity)level.getBlockEntity(pos);
            if(crateBlockEntity == null) {
                return InteractionResult.PASS;
            }

            ProbabilitiesMenu menu = new ProbabilitiesMenu(serverPlayer, null) {
                @Override
                public String getTranslationKey() {
                    return crateBlockEntity.getIdentifiedTranslationKey();
                }
            };

            menu.open();
            return InteractionResult.SUCCESS;
        }

        //TODO
        player.sendSystemMessage(
            Component.literal("TODO: key-use and/or rejection-message")
        );
        return InteractionResult.SUCCESS;
    }
}
