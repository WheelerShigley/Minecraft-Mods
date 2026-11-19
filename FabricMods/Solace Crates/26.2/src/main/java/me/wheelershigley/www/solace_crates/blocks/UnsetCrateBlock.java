package me.wheelershigley.www.solace_crates.blocks;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.wheelershigley.www.solace_crates.SolaceCrates;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class UnsetCrateBlock extends Block implements PolymerTexturedBlock {
    private final BlockState STATE;

    public UnsetCrateBlock(BlockBehaviour.Properties properties) {
        super(properties);

        STATE = PolymerBlockResourceUtils.requestBlock(
            BlockModelType.FULL_BLOCK,
            PolymerBlockModel.of(
                SolaceCrates.id("block/unset_crate")
            )
        );
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, @Nullable PacketContext context) {
        return STATE;
    }
}
