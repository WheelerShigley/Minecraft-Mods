package me.wheelershigley.www.solace_crates.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootTable.Builder.class)
public interface LootTableBuilderAccessor {
    @Accessor("pools")
    ImmutableList.Builder<LootPool> solace_crates$getPools();
}
