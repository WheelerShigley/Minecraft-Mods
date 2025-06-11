package www.wheelershigley.me.additional_crafting.client;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class AdditionalCraftingRecipeProvider extends FabricRecipeProvider {
    public AdditionalCraftingRecipeProvider(
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
//                RegistryWrapper.Impl<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);

                createShapeless(RecipeCategory.MISC, Items.AMETHYST_SHARD, 4)
                    .input(Items.AMETHYST_BLOCK)
                    .criterion(
                        hasItem(Items.AMETHYST_BLOCK),
                        conditionsFromItem(Items.AMETHYST_BLOCK)
                    )
                    .offerTo(exporter)
                ;
            }
        };
    }

    @Override
    public String getName() {
        return "AdditionalCraftingRecipeProvider";
    }
}
