package me.wheelershigley.www.solace_crates.blocks;

import me.wheelershigley.www.solace_crates.SolaceCrates;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;


public class CrateBlockEntity extends BlockEntity {
    private Identifier crateIdentifier;

    public CrateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRATE_BLOCK_ENTITY, pos, state);
    }

    public CrateBlockEntity(
        BlockEntityType<?> type,
        BlockPos worldPosition,
        BlockState blockState
    ) {
        super(type, worldPosition, blockState);
    }

    public Identifier getCrateIdentifier() {
        return crateIdentifier;
    }
    private static final Identifier DEFAULT_IDENTIFIER = Identifier.withDefaultNamespace("");
    public String getIdentifiedTranslationKey() {
        Identifier identifier = getCrateIdentifier();
        if( identifier.equals(DEFAULT_IDENTIFIER) ) {
            return SolaceCrates.MOD_ID + ".crate.unknown.name";
        }
        return
            identifier.getNamespace() +
            ".crate." +
            identifier.getPath() +
            ".name"
        ;
    }

    public void setCrateIdentifier(Identifier identifier) {
        this.crateIdentifier = identifier;
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);

        output.putString( "crate", crateIdentifier.toString() );
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        String identifierString = input.getStringOr("crate", "");
        Identifier identifier = Identifier.tryParse(identifierString);
        if(identifier != null) {
            crateIdentifier = identifier;
        }
    }
}