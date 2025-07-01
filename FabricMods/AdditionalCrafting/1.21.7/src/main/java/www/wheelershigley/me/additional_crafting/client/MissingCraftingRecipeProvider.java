package www.wheelershigley.me.additional_crafting.client;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class MissingCraftingRecipeProvider extends FabricRecipeProvider {
    public MissingCraftingRecipeProvider(
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
//                createShaped(RecipeCategory.MISC, Items.WARPED_WART_BLOCK, 1)
//                    .pattern("www")
//                    .pattern("www")
//                    .pattern("www")
//                    .input('w', Items.NETHER_WART)
//                    .criterion(
//                            hasItem(Items.WARPED_WART_BLOCK),
//                            conditionsFromItem(Items.WARPED_WART_BLOCK)
//                    )
//                    .offerTo(exporter)
//                ;
            }
        };
    }

    @Override
    public String getName() {
        return "MissingCraftingRecipeProvider";
    }
}
