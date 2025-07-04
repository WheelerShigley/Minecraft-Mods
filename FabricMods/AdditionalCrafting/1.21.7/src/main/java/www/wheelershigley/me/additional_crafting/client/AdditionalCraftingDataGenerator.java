package www.wheelershigley.me.additional_crafting.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class AdditionalCraftingDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(DeCraftingRecipeProvider::new);
        //pack.addProvider(MissingCraftingRecipeProvider::new);
        pack.addProvider(CompatibilityCraftingRecipeProvider::new);
        pack.addProvider(SawMillRecipeProvider::new);
        pack.addProvider(StoneCutterRecipeProvider::new);
    }
}
