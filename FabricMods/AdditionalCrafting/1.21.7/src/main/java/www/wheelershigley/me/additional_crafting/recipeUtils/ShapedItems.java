package www.wheelershigley.me.additional_crafting.recipeUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class ShapedItems {
    public static final ShapeableItem[] LOGS = new ShapeableItem[]{
        //Oak
        new ShapeableItem(
            Items.OAK_LOG,
            List.of( new ItemStack(Items.OAK_PLANKS, 4) ),
            List.of( new ItemStack(Items.OAK_WOOD,1), new ItemStack(Items.STRIPPED_OAK_LOG, 1), new ItemStack(Items.STRIPPED_OAK_WOOD, 1) ),
            List.of( new ItemStack(Items.OAK_STAIRS, 1) ),
            List.of( new ItemStack(Items.OAK_SLAB, 2) ),
            List.of( new ItemStack(Items.OAK_FENCE, 1) ),
            List.of( new ItemStack(Items.OAK_DOOR, 1) ),
            List.of( new ItemStack(Items.OAK_TRAPDOOR, 8) )
        ),
        new ShapeableItem(
            Items.STRIPPED_OAK_LOG,
            List.of( new ItemStack(Items.OAK_PLANKS, 4) ),
            List.of( new ItemStack(Items.STRIPPED_OAK_WOOD, 1) ),
            List.of( new ItemStack(Items.OAK_STAIRS, 1) ),
            List.of( new ItemStack(Items.OAK_SLAB, 2) ),
            List.of( new ItemStack(Items.OAK_FENCE, 1) ),
            List.of( new ItemStack(Items.OAK_DOOR, 1) ),
            List.of( new ItemStack(Items.OAK_TRAPDOOR, 8) )
        ),
        //Birch
        new ShapeableItem(
            Items.BIRCH_LOG,
            List.of( new ItemStack(Items.BIRCH_PLANKS, 4) ),
            List.of( new ItemStack(Items.BIRCH_WOOD,1), new ItemStack(Items.STRIPPED_BIRCH_LOG, 1), new ItemStack(Items.STRIPPED_BIRCH_WOOD, 1) ),
            List.of( new ItemStack(Items.BIRCH_STAIRS, 1) ),
            List.of( new ItemStack(Items.BIRCH_SLAB, 2) ),
            List.of( new ItemStack(Items.BIRCH_FENCE, 1) ),
            List.of( new ItemStack(Items.BIRCH_DOOR, 1) ),
            List.of( new ItemStack(Items.BIRCH_TRAPDOOR, 8) )
        ),
        new ShapeableItem(
            Items.STRIPPED_BIRCH_LOG,
            List.of( new ItemStack(Items.BIRCH_PLANKS, 4) ),
            List.of( new ItemStack(Items.STRIPPED_BIRCH_WOOD, 1) ),
            List.of( new ItemStack(Items.BIRCH_STAIRS, 1) ),
            List.of( new ItemStack(Items.BIRCH_SLAB, 2) ),
            List.of( new ItemStack(Items.BIRCH_FENCE, 1) ),
            List.of( new ItemStack(Items.BIRCH_DOOR, 1) ),
            List.of( new ItemStack(Items.BIRCH_TRAPDOOR, 8) )
        ),
        //Spruce
        new ShapeableItem(
            Items.SPRUCE_LOG,
            List.of( new ItemStack(Items.SPRUCE_PLANKS, 4) ),
            List.of( new ItemStack(Items.SPRUCE_WOOD,1), new ItemStack(Items.STRIPPED_SPRUCE_LOG, 1), new ItemStack(Items.STRIPPED_SPRUCE_WOOD, 1) ),
            List.of( new ItemStack(Items.SPRUCE_STAIRS, 1) ),
            List.of( new ItemStack(Items.SPRUCE_SLAB, 2) ),
            List.of( new ItemStack(Items.SPRUCE_FENCE, 1) ),
            List.of( new ItemStack(Items.SPRUCE_DOOR, 1) ),
            List.of( new ItemStack(Items.SPRUCE_TRAPDOOR, 8) )
        ),
        new ShapeableItem(
            Items.STRIPPED_SPRUCE_LOG,
            List.of( new ItemStack(Items.SPRUCE_PLANKS, 4) ),
            List.of( new ItemStack(Items.STRIPPED_SPRUCE_WOOD, 1) ),
            List.of( new ItemStack(Items.SPRUCE_STAIRS, 1) ),
            List.of( new ItemStack(Items.SPRUCE_SLAB, 2) ),
            List.of( new ItemStack(Items.SPRUCE_FENCE, 1) ),
            List.of( new ItemStack(Items.SPRUCE_DOOR, 1) ),
            List.of( new ItemStack(Items.SPRUCE_TRAPDOOR, 8) )
        ),
        //Jungle
        new ShapeableItem(
            Items.JUNGLE_LOG,
            List.of( new ItemStack(Items.JUNGLE_PLANKS, 4) ),
            List.of( new ItemStack(Items.JUNGLE_WOOD,1), new ItemStack(Items.STRIPPED_JUNGLE_LOG, 1), new ItemStack(Items.STRIPPED_JUNGLE_WOOD, 1) ),
            List.of( new ItemStack(Items.JUNGLE_STAIRS, 1) ),
            List.of( new ItemStack(Items.JUNGLE_SLAB, 2) ),
            List.of( new ItemStack(Items.JUNGLE_FENCE, 1) ),
            List.of( new ItemStack(Items.JUNGLE_DOOR, 1) ),
            List.of( new ItemStack(Items.JUNGLE_TRAPDOOR, 8) )
        ),
        new ShapeableItem(
            Items.STRIPPED_JUNGLE_LOG,
            List.of( new ItemStack(Items.JUNGLE_PLANKS, 4) ),
            List.of( new ItemStack(Items.STRIPPED_JUNGLE_WOOD, 1) ),
            List.of( new ItemStack(Items.JUNGLE_STAIRS, 1) ),
            List.of( new ItemStack(Items.JUNGLE_SLAB, 2) ),
            List.of( new ItemStack(Items.JUNGLE_FENCE, 1) ),
            List.of( new ItemStack(Items.JUNGLE_DOOR, 1) ),
            List.of( new ItemStack(Items.JUNGLE_TRAPDOOR, 8) )
        ),
        //Dark-Oak
        new ShapeableItem(
            Items.DARK_OAK_LOG,
            List.of( new ItemStack(Items.DARK_OAK_PLANKS, 4) ),
            List.of( new ItemStack(Items.DARK_OAK_WOOD,1), new ItemStack(Items.STRIPPED_DARK_OAK_LOG, 1), new ItemStack(Items.STRIPPED_DARK_OAK_WOOD, 1) ),
            List.of( new ItemStack(Items.DARK_OAK_STAIRS, 1) ),
            List.of( new ItemStack(Items.DARK_OAK_SLAB, 2) ),
            List.of( new ItemStack(Items.DARK_OAK_FENCE, 1) ),
            List.of( new ItemStack(Items.DARK_OAK_DOOR, 1) ),
            List.of( new ItemStack(Items.DARK_OAK_TRAPDOOR, 8) )
        ),
        new ShapeableItem(
            Items.STRIPPED_DARK_OAK_LOG,
            List.of( new ItemStack(Items.DARK_OAK_PLANKS, 4) ),
            List.of( new ItemStack(Items.STRIPPED_DARK_OAK_WOOD, 1) ),
            List.of( new ItemStack(Items.DARK_OAK_STAIRS, 1) ),
            List.of( new ItemStack(Items.DARK_OAK_SLAB, 2) ),
            List.of( new ItemStack(Items.DARK_OAK_FENCE, 1) ),
            List.of( new ItemStack(Items.DARK_OAK_DOOR, 1) ),
            List.of( new ItemStack(Items.DARK_OAK_TRAPDOOR, 8) )
        ),
        //Acacia
        new ShapeableItem(
            Items.ACACIA_LOG,
            List.of( new ItemStack(Items.ACACIA_PLANKS, 4) ),
            List.of( new ItemStack(Items.ACACIA_WOOD,1), new ItemStack(Items.STRIPPED_ACACIA_LOG, 1), new ItemStack(Items.STRIPPED_ACACIA_WOOD, 1) ),
            List.of( new ItemStack(Items.ACACIA_STAIRS, 1) ),
            List.of( new ItemStack(Items.ACACIA_SLAB, 2) ),
            List.of( new ItemStack(Items.ACACIA_FENCE, 1) ),
            List.of( new ItemStack(Items.ACACIA_DOOR, 1) ),
            List.of( new ItemStack(Items.ACACIA_TRAPDOOR, 8) )
        ),
        new ShapeableItem(
            Items.STRIPPED_ACACIA_LOG,
            List.of( new ItemStack(Items.ACACIA_PLANKS, 4) ),
            List.of( new ItemStack(Items.STRIPPED_ACACIA_WOOD, 1) ),
            List.of( new ItemStack(Items.ACACIA_STAIRS, 1) ),
            List.of( new ItemStack(Items.ACACIA_SLAB, 2) ),
            List.of( new ItemStack(Items.ACACIA_FENCE, 1) ),
            List.of( new ItemStack(Items.ACACIA_DOOR, 1) ),
            List.of( new ItemStack(Items.ACACIA_TRAPDOOR, 8) )
        ),
        //Bamboo
        new ShapeableItem(
            Items.BAMBOO_BLOCK,
            List.of( new ItemStack(Items.BAMBOO_PLANKS, 2) ),
            List.of( new ItemStack(Items.STRIPPED_BAMBOO_BLOCK, 1) ),
            List.of( new ItemStack(Items.BAMBOO_STAIRS, 2) ),
            List.of( new ItemStack(Items.BAMBOO_SLAB, 4) ),
            List.of( new ItemStack(Items.BAMBOO_FENCE, 1) ),
            List.of( new ItemStack(Items.BAMBOO_DOOR, 1) ),
            List.of( new ItemStack(Items.BAMBOO_TRAPDOOR, 4) )
        ),
        new ShapeableItem(
            Items.BAMBOO_BLOCK,
            List.of( new ItemStack(Items.BAMBOO_MOSAIC, 1) ),
            List.of(),
            List.of( new ItemStack(Items.BAMBOO_MOSAIC_STAIRS, 2) ),
            List.of( new ItemStack(Items.BAMBOO_MOSAIC_SLAB, 4) ),
            List.of(),
            List.of(),
            List.of()
        ),
            new ShapeableItem(
                Items.STRIPPED_BAMBOO_BLOCK,
                List.of( new ItemStack(Items.BAMBOO_PLANKS, 2) ),
                List.of(),
                List.of( new ItemStack(Items.BAMBOO_STAIRS, 2) ),
                List.of( new ItemStack(Items.BAMBOO_SLAB, 4) ),
                List.of( new ItemStack(Items.BAMBOO_FENCE, 1) ),
                List.of( new ItemStack(Items.BAMBOO_DOOR, 1) ),
                List.of( new ItemStack(Items.BAMBOO_TRAPDOOR, 4) )
            ),
            new ShapeableItem(
                Items.STRIPPED_BAMBOO_BLOCK,
                List.of( new ItemStack(Items.BAMBOO_MOSAIC, 1) ),
                List.of(),
                List.of( new ItemStack(Items.BAMBOO_MOSAIC_STAIRS, 2) ),
                List.of( new ItemStack(Items.BAMBOO_MOSAIC_SLAB, 4) ),
                List.of(),
                List.of(),
                List.of()
            ),
        //Cherry
        new ShapeableItem(
            Items.CHERRY_LOG,
            List.of( new ItemStack(Items.CHERRY_PLANKS, 4) ),
            List.of( new ItemStack(Items.CHERRY_WOOD,1), new ItemStack(Items.STRIPPED_CHERRY_LOG, 1), new ItemStack(Items.STRIPPED_CHERRY_WOOD, 1) ),
            List.of( new ItemStack(Items.CHERRY_STAIRS, 1) ),
            List.of( new ItemStack(Items.CHERRY_SLAB, 2) ),
            List.of( new ItemStack(Items.CHERRY_FENCE, 1) ),
            List.of( new ItemStack(Items.CHERRY_DOOR, 1) ),
            List.of( new ItemStack(Items.CHERRY_TRAPDOOR, 8) )
        ),
        new ShapeableItem(
            Items.STRIPPED_CHERRY_LOG,
            List.of( new ItemStack(Items.CHERRY_PLANKS, 4) ),
            List.of( new ItemStack(Items.STRIPPED_CHERRY_WOOD, 1) ),
            List.of( new ItemStack(Items.CHERRY_STAIRS, 1) ),
            List.of( new ItemStack(Items.CHERRY_SLAB, 2) ),
            List.of( new ItemStack(Items.CHERRY_FENCE, 1) ),
            List.of( new ItemStack(Items.CHERRY_DOOR, 1) ),
            List.of( new ItemStack(Items.CHERRY_TRAPDOOR, 8) )
        ),
        //Mangrove
        new ShapeableItem(
            Items.MANGROVE_LOG,
            List.of( new ItemStack(Items.MANGROVE_PLANKS, 4) ),
            List.of( new ItemStack(Items.MANGROVE_WOOD,1), new ItemStack(Items.STRIPPED_MANGROVE_LOG, 1), new ItemStack(Items.STRIPPED_MANGROVE_WOOD, 1) ),
            List.of( new ItemStack(Items.MANGROVE_STAIRS, 1) ),
            List.of( new ItemStack(Items.MANGROVE_SLAB, 2) ),
            List.of( new ItemStack(Items.MANGROVE_FENCE, 1) ),
            List.of( new ItemStack(Items.MANGROVE_DOOR, 1) ),
            List.of( new ItemStack(Items.MANGROVE_TRAPDOOR, 8) )
        ),
        new ShapeableItem(
            Items.STRIPPED_MANGROVE_LOG,
            List.of( new ItemStack(Items.MANGROVE_PLANKS, 4) ),
            List.of( new ItemStack(Items.STRIPPED_MANGROVE_WOOD, 1) ),
            List.of( new ItemStack(Items.MANGROVE_STAIRS, 1) ),
            List.of( new ItemStack(Items.MANGROVE_SLAB, 2) ),
            List.of( new ItemStack(Items.MANGROVE_FENCE, 1) ),
            List.of( new ItemStack(Items.MANGROVE_DOOR, 1) ),
            List.of( new ItemStack(Items.MANGROVE_TRAPDOOR, 8) )
        ),
        //Pale-Oak
        new ShapeableItem(
            Items.PALE_OAK_LOG,
            List.of( new ItemStack(Items.PALE_OAK_PLANKS, 4) ),
            List.of( new ItemStack(Items.PALE_OAK_WOOD,1), new ItemStack(Items.STRIPPED_PALE_OAK_LOG, 1), new ItemStack(Items.STRIPPED_PALE_OAK_WOOD, 1) ),
            List.of( new ItemStack(Items.PALE_OAK_STAIRS, 1) ),
            List.of( new ItemStack(Items.PALE_OAK_SLAB, 2) ),
            List.of( new ItemStack(Items.PALE_OAK_FENCE, 1) ),
            List.of( new ItemStack(Items.PALE_OAK_DOOR, 1) ),
            List.of( new ItemStack(Items.PALE_OAK_TRAPDOOR, 8) )
        ),
        new ShapeableItem(
            Items.STRIPPED_PALE_OAK_LOG,
            List.of( new ItemStack(Items.PALE_OAK_PLANKS, 4) ),
            List.of( new ItemStack(Items.STRIPPED_PALE_OAK_WOOD, 1) ),
            List.of( new ItemStack(Items.PALE_OAK_STAIRS, 1) ),
            List.of( new ItemStack(Items.PALE_OAK_SLAB, 2) ),
            List.of( new ItemStack(Items.PALE_OAK_FENCE, 1) ),
            List.of( new ItemStack(Items.PALE_OAK_DOOR, 1) ),
            List.of( new ItemStack(Items.PALE_OAK_TRAPDOOR, 8) )
        ),
    };

    public static final ShapeableItem[] STONES = new ShapeableItem[]{
        new ShapeableItem(
            Items.DEEPSLATE,
            List.of( new ItemStack(Items.CHISELED_DEEPSLATE, 1), new ItemStack(Items.COBBLED_DEEPSLATE, 1) ),
            List.of( new ItemStack(Items.POLISHED_DEEPSLATE, 1), new ItemStack(Items.DEEPSLATE_BRICKS, 1), new ItemStack(Items.DEEPSLATE_TILES, 1) ),
            List.of( new ItemStack(Items.COBBLED_DEEPSLATE_STAIRS, 1), new ItemStack(Items.POLISHED_DEEPSLATE_STAIRS, 1), new ItemStack(Items.DEEPSLATE_BRICK_STAIRS, 1), new ItemStack(Items.DEEPSLATE_TILE_STAIRS, 1) ),
            List.of( new ItemStack(Items.COBBLED_DEEPSLATE_SLAB, 2), new ItemStack(Items.POLISHED_DEEPSLATE_SLAB, 2), new ItemStack(Items.DEEPSLATE_BRICK_SLAB, 2), new ItemStack(Items.DEEPSLATE_TILE_SLAB, 2) ),
            List.of( new ItemStack(Items.COBBLED_DEEPSLATE_WALL, 1), new ItemStack(Items.POLISHED_DEEPSLATE_WALL, 1), new ItemStack(Items.DEEPSLATE_BRICK_WALL, 1), new ItemStack(Items.DEEPSLATE_TILE_WALL, 1) ),
            List.of(),
            List.of()
        )
    };
}
