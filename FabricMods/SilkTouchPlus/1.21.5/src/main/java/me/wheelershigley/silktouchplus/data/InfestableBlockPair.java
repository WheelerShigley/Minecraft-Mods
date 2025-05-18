package me.wheelershigley.silktouchplus.data;

import net.minecraft.block.Block;

public class InfestableBlockPair {
    private final Block uninfested, infested;
    public InfestableBlockPair(Block uninfested, Block infested) {
        this.uninfested = uninfested;
        this.infested = infested;
    }

    public Block getUninfestedBlock() {
        return this.uninfested;
    }
    public Block getInfestedBlock() {
        return this.infested;
    }
}
