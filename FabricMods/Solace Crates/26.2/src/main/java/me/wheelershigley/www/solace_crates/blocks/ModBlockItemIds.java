package me.wheelershigley.www.solace_crates.blocks;

import me.wheelershigley.www.solace_crates.SolaceCrates;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;

public class ModBlockItemIds {
    public static final BlockItemId
        CRATE = createBlock(CrateBlock.NAME)
    ;

    private static BlockItemId createBlock(String name) {
        Identifier identifier = SolaceCrates.id(name);
        return BlockItemId.create(identifier, identifier);
    }
}
