package www.wheelershigley.me.additional_crafting.client;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import www.wheelershigley.me.additional_crafting.recipeUtils.ShapeableItem;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static www.wheelershigley.me.additional_crafting.recipeUtils.ShapedItems.LOGS;
import static www.wheelershigley.me.additional_crafting.recipeUtils.ShapedItems.STONES;
import static www.wheelershigley.me.additional_crafting.recipeUtils.listHelper.listOfLists;

public class StoneCutterRecipeProvider extends FabricRecipeProvider {
    public StoneCutterRecipeProvider(
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
            List<ItemStack> results;
            @Override
            public void generate() {
                for(ShapeableItem STONE : STONES) {
                    results = listOfLists(
                        STONE.chiseled, STONE.polished,
                        STONE.stair, STONE.slab, STONE.wall,
                        STONE.door, STONE.trapdoor
                    );
                    for(ItemStack result : results) {
                        offerStonecuttingRecipe(
                            RecipeCategory.BUILDING_BLOCKS,
                            result.getItem(),
                            STONE.item,
                            result.getCount()
                        );
                    }
                }
            }
        };
    }

    @Override
    public String getName() {
        return "StoneCutterRecipeProvider";
    }
}
