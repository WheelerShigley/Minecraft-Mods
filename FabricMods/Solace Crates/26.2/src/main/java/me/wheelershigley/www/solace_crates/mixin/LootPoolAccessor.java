package me.wheelershigley.www.solace_crates.mixin;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Predicate;

@Mixin(LootPool.class)
public interface LootPoolAccessor {
    @Accessor("rolls")
    NumberProvider solace_crates$getRolls();
    @Accessor("bonusRolls")
    NumberProvider solace_crates$getBonusRolls();

    @Accessor("compositeCondition")
    Predicate<LootContext> solace_crates$getCompositeCondition();
}
