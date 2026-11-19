package me.wheelershigley.www.solace_crates.blocks;

import me.wheelershigley.www.solace_crates.SolaceCrates;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;

public class ModBlockItemIds {
    public static final BlockItemId
        CRATE = createBlock( CrateBlock.DEFAULT_BLOCK_IDENTIFIER ),
        UNSET_CRATE = createBlock( SolaceCrates.id("unset_crate") )
    ;

    private static BlockItemId createBlock(Identifier identifier) {
        return BlockItemId.create(identifier, identifier);
    }
}
