package me.wheelershigley.www.solace_crates.blocks;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.core.api.block.SimplePolymerBlock;
import me.wheelershigley.www.solace_crates.SolaceCrates;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
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
    protected InteractionResult useItemOn(
        final ItemStack itemStack,
        final BlockState state, final Level level, final BlockPos pos,
        final Player player, final InteractionHand hand, final BlockHitResult hitResult
    ) {
        if( player.isCrouching() ) {
            player.sendSystemMessage(
                Component.literal("TODO: Probabilities Menu")
            );
            return InteractionResult.SUCCESS;
        }

        player.sendSystemMessage(
            Component.literal("TODO: key-use and/or rejection-message")
        );
        return InteractionResult.SUCCESS;
    }
}
