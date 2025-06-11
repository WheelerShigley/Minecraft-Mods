package www.wheelershigley.me.additional_crafting.client;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
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
                createShapeless(RecipeCategory.MISC, Items.AMETHYST_SHARD, 4)
                    .input(Items.AMETHYST_BLOCK)
                    .criterion(
                        hasItem(Items.AMETHYST_BLOCK),
                        conditionsFromItem(Items.AMETHYST_BLOCK)
                    )
                    .offerTo(exporter)
                ;

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
            }
        };
    }

    @Override
    public String getName() {
        return "DeCraftingRecipeProvider";
    }
}
