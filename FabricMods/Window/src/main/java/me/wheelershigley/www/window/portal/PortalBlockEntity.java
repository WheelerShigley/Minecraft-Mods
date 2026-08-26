package me.wheelershigley.www.window.portal;

import me.wheelershigley.www.window.registrations.WindowBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PortalBlockEntity extends BlockEntity {
    private Block frame;
    private Block igniter;

    public PortalBlockEntity(BlockPos pos, BlockState state, DyeColor color) {
        super(getBlockEntityType(color), pos, state);
    }

    private static BlockEntityType<PortalBlockEntity> getBlockEntityType(DyeColor color) {
        return switch (color) {
            case WHITE ->       WindowBlockEntities.WHITE_PORTAL;
            case LIGHT_GRAY ->  WindowBlockEntities.LIGHT_GRAY_PORTAL;
            case GRAY ->        WindowBlockEntities.GRAY_PORTAL;
            case BLACK ->       WindowBlockEntities.BLACK_PORTAL;
            case BROWN ->       WindowBlockEntities.BROWN_PORTAL;
            case RED ->         WindowBlockEntities.RED_PORTAL;
            case ORANGE ->      WindowBlockEntities.ORANGE_PORTAL;
            case YELLOW ->      WindowBlockEntities.YELLOW_PORTAL;
            case LIME ->        WindowBlockEntities.LIME_PORTAL;
            case GREEN ->       WindowBlockEntities.GREEN_PORTAL;
            case CYAN ->        WindowBlockEntities.CYAN_PORTAL;
            case LIGHT_BLUE ->  WindowBlockEntities.LIGHT_BLUE_PORTAL;
            case BLUE ->        WindowBlockEntities.BLUE_PORTAL;
            case PURPLE ->      WindowBlockEntities.PURPLE_PORTAL;
            case MAGENTA ->     WindowBlockEntities.MAGENTA_PORTAL;
            case PINK ->        WindowBlockEntities.PINK_PORTAL;
        };
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
