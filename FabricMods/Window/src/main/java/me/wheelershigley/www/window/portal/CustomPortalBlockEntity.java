package me.wheelershigley.www.window.portal;

import me.wheelershigley.www.window.WindowBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CustomPortalBlockEntity extends BlockEntity {
    private Block frame;
    private Block igniter;

    public CustomPortalBlockEntity(BlockPos pos, BlockState state) {
        super(WindowBlockEntities.CUSTOM_PORTAL, pos, state);
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);

        output.store(
            "frame",
            Identifier.CODEC,
            BuiltInRegistries.BLOCK.getKey(frame)
        );

        output.store(
            "igniter",
            Identifier.CODEC,
            BuiltInRegistries.BLOCK.getKey(igniter)
        );
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);

        input.read("frame", Identifier.CODEC)
            .flatMap(BuiltInRegistries.BLOCK::getOptional)
            .ifPresent(
                block -> this.frame = block
            )
        ;

        input.read("igniter", Identifier.CODEC)
            .flatMap(BuiltInRegistries.BLOCK::getOptional)
            .ifPresent(
                block -> this.igniter = block
            )
        ;
    }

    public Block getFrame() {
        return frame;
    }
    public Block getIgniter() {
        return igniter;
    }

    public void setFrame(Block frame) {
        this.frame = frame;
        this.setChanged();
    }
    public void setIgniter(Block igniter) {
        this.igniter = igniter;
        this.setChanged();
    }
}
