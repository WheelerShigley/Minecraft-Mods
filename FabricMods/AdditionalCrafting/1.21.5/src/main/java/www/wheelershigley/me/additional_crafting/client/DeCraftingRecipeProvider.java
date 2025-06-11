package www.wheelershigley.me.additional_crafting.client;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class DeCraftingRecipeProvider extends FabricRecipeProvider {
    public DeCraftingRecipeProvider(
        FabricDataOutput output,
        CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(
        RegistryWrapper.WrapperLookup wrapperLookup,
        RecipeExporter recipeExporter
    ) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                //Amethyst Shard
                offerStonecuttingRecipe(RecipeCategory.MISC, Items.AMETHYST_SHARD, Items.AMETHYST_BLOCK, 4);

                //Nether Warts
                createShapeless(RecipeCategory.MISC, Items.NETHER_WART, 9)
                    .input(Items.NETHER_WART_BLOCK)
                    .criterion(
                        hasItem(Items.NETHER_WART_BLOCK),
                        conditionsFromItem(Items.NETHER_WART_BLOCK)
                    )
                    .offerTo(exporter, "nether_wart_nether_wart")
                ;
                createShapeless(RecipeCategory.MISC, Items.NETHER_WART, 9)
                    .input(Items.WARPED_WART_BLOCK)
                    .criterion(
                        hasItem(Items.WARPED_WART_BLOCK),
                        conditionsFromItem(Items.WARPED_WART_BLOCK)
                    )
                    .offerTo(exporter, "warped_nether_wart")
                ;

                //Prismarines
                offerStonecuttingRecipe(RecipeCategory.MISC, Items.PRISMARINE_SHARD, Items.PRISMARINE, 4);
                offerStonecuttingRecipe(RecipeCategory.MISC, Items.PRISMARINE_SHARD, Items.PRISMARINE_BRICKS, 9);
                offerStonecuttingRecipe(RecipeCategory.MISC, Items.PRISMARINE_SHARD, Items.DARK_PRISMARINE, 8);

                //Quartzs
                createShapeless(RecipeCategory.MISC, Items.QUARTZ, 4)
                    .input(Items.QUARTZ_BLOCK)
                    .criterion(
                        hasItem(Items.QUARTZ_BLOCK),
                        conditionsFromItem(Items.QUARTZ_BLOCK)
                    )
                    .offerTo(exporter, "quartz_block_quartz")
                ;
                createShapeless(RecipeCategory.MISC, Items.QUARTZ, 4)
                    .input(Items.SMOOTH_QUARTZ)
                    .criterion(
                            hasItem(Items.SMOOTH_QUARTZ),
                            conditionsFromItem(Items.SMOOTH_QUARTZ)
                    )
                    .offerTo(exporter, "smooth_quartz_block_quartz")
                ;
                createShapeless(RecipeCategory.MISC, Items.QUARTZ, 4)
                    .input(Items.CHISELED_QUARTZ_BLOCK)
                    .criterion(
                        hasItem(Items.CHISELED_QUARTZ_BLOCK),
                        conditionsFromItem(Items.CHISELED_QUARTZ_BLOCK)
                    )
                    .offerTo(exporter, "chiseled_quartz_block_quartz")
                ;
                createShapeless(RecipeCategory.MISC, Items.QUARTZ, 4)
                    .input(Items.QUARTZ_BRICKS)
                    .criterion(
                            hasItem(Items.QUARTZ_BRICKS),
                            conditionsFromItem(Items.QUARTZ_BRICKS)
                    )
                    .offerTo(exporter, "quartz_bricks_block_quartz")
                ;
                createShapeless(RecipeCategory.MISC, Items.QUARTZ, 4)
                    .input(Items.QUARTZ_PILLAR)
                    .criterion(
                        hasItem(Items.QUARTZ_PILLAR),
                        conditionsFromItem(Items.QUARTZ_PILLAR)
                    )
                    .offerTo(exporter, "quartz_pillar_block_quartz")
                ;

                //Sands
                createShapeless(RecipeCategory.MISC, Items.SAND, 4)
                    .input(Items.SANDSTONE)
                    .criterion(
                        hasItem(Items.SANDSTONE),
                        conditionsFromItem(Items.SANDSTONE)
                    )
                    .offerTo(exporter, "sandstone_sand")
                ;
                createShapeless(RecipeCategory.MISC, Items.SAND, 4)
                    .input(Items.SMOOTH_SANDSTONE)
                    .criterion(
                        hasItem(Items.SMOOTH_SANDSTONE),
                        conditionsFromItem(Items.SMOOTH_SANDSTONE)
                    )
                    .offerTo(exporter, "smooth_sandstone_sand")
                ;
                createShapeless(RecipeCategory.MISC, Items.RED_SAND, 4)
                    .input(Items.RED_SANDSTONE)
                    .criterion(
                        hasItem(Items.RED_SANDSTONE),
                        conditionsFromItem(Items.RED_SANDSTONE)
                    )
                    .offerTo(exporter, "red_sandstone_red_sand")
                ;
                createShapeless(RecipeCategory.MISC, Items.RED_SAND, 4)
                    .input(Items.SMOOTH_RED_SANDSTONE)
                    .criterion(
                        hasItem(Items.SMOOTH_RED_SANDSTONE),
                        conditionsFromItem(Items.SMOOTH_RED_SANDSTONE)
                    )
                    .offerTo(exporter, "smooth_red_sandstone_red_sand")
                ;

                //Bricks
                createShapeless(RecipeCategory.MISC, Items.BRICK, 4)
                    .input(Items.BRICKS)
                    .criterion(
                        hasItem(Items.BRICKS),
                        conditionsFromItem(Items.BRICKS)
                    )
                    .offerTo(exporter)
                ;
                createShaped(RecipeCategory.MISC, Items.PACKED_MUD, 4)
                    .pattern("bb")
                    .pattern("bb")
                    .input('b', Items.MUD_BRICKS)
                    .criterion(
                        hasItem(Items.MUD_BRICKS),
                        conditionsFromItem(Items.MUD_BRICKS)
                    )
                    .offerTo(exporter)
                ;
                createShaped(RecipeCategory.MISC, Items.END_STONE, 4)
                    .pattern("bb")
                    .pattern("bb")
                    .input('b', Items.END_STONE_BRICKS)
                    .criterion(
                        hasItem(Items.END_STONE_BRICKS),
                        conditionsFromItem(Items.END_STONE_BRICKS)
                    )
                    .offerTo(exporter)
                ;
                createShapeless(RecipeCategory.MISC, Items.NETHER_BRICK, 4)
                    .input(Items.NETHER_BRICKS)
                    .criterion(
                        hasItem(Items.NETHER_BRICKS),
                        conditionsFromItem(Items.NETHER_BRICKS)
                    )
                    .offerTo(exporter, "nether_bricks_nether_brick")
                ;
                createShapeless(RecipeCategory.MISC, Items.NETHER_BRICK, 2)
                    .input(Items.RED_NETHER_BRICKS)
                    .criterion(
                        hasItem(Items.RED_NETHER_BRICKS),
                        conditionsFromItem(Items.RED_NETHER_BRICKS)
                    )
                    .offerTo(exporter, "red_nether_bricks_nether_brick")
                ;

            }
        };
    }

    @Override
    public String getName() {
        return "DeCraftingRecipeProvider";
    }
}
