//package me.wheelershigley.solstraders;
//
//import net.minecraft.block.Block;
//import net.minecraft.data.DataOutput;
//import net.minecraft.data.tag.ItemTagProvider;
//import net.minecraft.registry.RegistryWrapper;
//
//import java.util.concurrent.CompletableFuture;

//public class ModItemTagProider extends ItemTagProvider {
//    public ModItemTagProider(
//        DataOutput output,
//        CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture,
//        CompletableFuture<TagLookup<Block>> blockTagLookupFuture
//    ) {
//        super(output, registriesFuture, blockTagLookupFuture);
//    }
//
//    @Override
//    protected void configure(RegistryWrapper.WrapperLookup registries) {
//        getOrCreateTagBuilder(ModTags.SPAWN_EGGS_HOSTILE).add(ItemGroups.hostileMobSpawnEggs);
//        getOrCreateTagBuilder(ModTags.SPAWN_EGGS_NEUTRAL).add(ItemGroups.neutralMobSpawnEggs);
//        getOrCreateTagBuilder(ModTags.SPAWN_EGGS_PASSIVE).add(ItemGroups.passiveMobSpawnEggs);
//    }
//}
