package me.wheelershigley.www.solace_crates.blocks;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.core.api.block.SimplePolymerBlock;
import me.wheelershigley.www.solace_crates.SolaceCrates;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jspecify.annotations.Nullable;

public class CrateBlock extends SimplePolymerBlock implements PolymerTexturedBlock {
    public static final String NAME = "crate";
    public static final BlockBehaviour.Properties PROPERTIES;
    private final BlockState STATE;

    public CrateBlock(Properties properties) {
        super(PROPERTIES, Blocks.BEDROCK);

        STATE = PolymerBlockResourceUtils.requestBlock(
            BlockModelType.FULL_BLOCK,
            PolymerBlockModel.of(
                SolaceCrates.id("block/" + NAME)
            )
        );
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, @Nullable PacketContext context) {
        return this.STATE;
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
