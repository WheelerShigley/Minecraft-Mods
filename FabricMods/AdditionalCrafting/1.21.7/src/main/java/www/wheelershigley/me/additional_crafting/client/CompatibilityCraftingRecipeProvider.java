package www.wheelershigley.me.additional_crafting.client;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class CompatibilityCraftingRecipeProvider extends FabricRecipeProvider {
    public CompatibilityCraftingRecipeProvider(
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
                createShaped(RecipeCategory.REDSTONE, Items.TNT, 1)
                    .pattern("gsg")
                    .pattern("sgs")
                    .pattern("gsg")
                    .input('g', Items.GUNPOWDER)
                    .input('s', Items.RED_SAND)
                    .criterion(
                        hasItem(Items.GUNPOWDER),
                        conditionsFromItem(Items.GUNPOWDER)
                    )
                    .offerTo(exporter, "red_sand_tnt")
                ;
            }
        };
    }

    @Override
    public String getName() {
        return "MissingCraftingRecipeProvider";
    }
}
