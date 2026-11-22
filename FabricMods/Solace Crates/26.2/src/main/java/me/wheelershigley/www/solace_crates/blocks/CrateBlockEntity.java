package me.wheelershigley.www.solace_crates.blocks;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import me.wheelershigley.www.solace_crates.SolaceCrates;
import me.wheelershigley.www.solace_crates.data.CrateDefinition;
import me.wheelershigley.www.solace_crates.registry.CrateRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;


public class CrateBlockEntity extends BlockEntity {
    public static final Identifier DEFAULT_IDENTIFIER = SolaceCrates.id("unset");
    private Identifier crateIdentifier = DEFAULT_IDENTIFIER;

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

    public String getIdentifiedTranslationKey() {
        Identifier identifier = getCrateIdentifier();
        return
            identifier.getNamespace() +
            ".crate." +
            identifier.getPath() +
            ".name"
        ;
    }
    public Identifier getCrateIdentifier() {
        return crateIdentifier;
    }

    public void setCrateIdentifier(Identifier identifier) {
        this.crateIdentifier = identifier;

        if(element != null) {
            element.setBlockState(
                getDisplayBlock().defaultBlockState()
            );
        }

        setChanged();
    }

    private @Nullable ServerLevel getServerLevel() {
        if(this.level == null) {
            return null;
        }

        if(this.level instanceof ServerLevel) {
            return (ServerLevel)this.level;
        }

        IntegratedServer singlePlayerServer = Minecraft.getInstance().getSingleplayerServer();
        if(singlePlayerServer == null) {
            return null;
        }
        return singlePlayerServer.getLevel( this.level.dimension() );
    }

    private BlockDisplayElement element = null;
    private ElementHolder displayHolder = null;
    public void refreshDisplay(ElementHolder holder) {
        ServerLevel serverLevel = getServerLevel();
        if(serverLevel == null) {
            return;
        }

        if(displayHolder != null && element != null) {
            displayHolder.removeElement(element);
        }
        displayHolder = holder;

        element = new BlockDisplayElement();
        element.setBlockState(
            getDisplayBlock().defaultBlockState()
        );
        element.setOffset(
            new Vec3(-0.5, -0.5, -0.5)
        );

        holder.addElement(element);
    }

    public Block getDisplayBlock() {
        CrateDefinition crateDefinition = getCrateDefinition();
        if(crateDefinition == null) {
            return ModBlocks.UNSET_CRATE;
        } else {
            Block crateBlock = BlocksHelper.getBlock(crateDefinition.MODEL());
            return (crateBlock == null ? ModBlocks.CRATE : crateBlock);
        }
    }

    private @Nullable CrateDefinition getCrateDefinition() {
        return CrateRegistry.get(this.crateIdentifier);
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