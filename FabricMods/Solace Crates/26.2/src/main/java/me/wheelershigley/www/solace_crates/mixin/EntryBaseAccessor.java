package me.wheelershigley.www.solace_crates.mixin;

import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
    targets = "net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer$EntryBase"
)
public interface EntryBaseAccessor {
    @Accessor(value = "this$0", remap = false)
    LootPoolSingletonContainer solace_crates$getContainer();
}
