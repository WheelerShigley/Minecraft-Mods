package me.wheelershigley.www.solace_crates.crate;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import me.wheelershigley.www.solace_crates.blocks.CrateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CrateElementHolder extends ElementHolder {
    private final ServerLevel world;
    private final BlockPos pos;
    private Block block = null;

    public CrateElementHolder(ServerLevel world, BlockPos pos) {
        this.world = world;
        this.pos = pos.immutable();
    }

    @Override
    protected void onTick() {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof CrateBlockEntity crateBlockEntity) {
            Block currentBlock = crateBlockEntity.getDisplayBlock();
            if(currentBlock == block) {
                return;
            }

            block = currentBlock;
            crateBlockEntity.refreshDisplay(this);
        }
    }
}
