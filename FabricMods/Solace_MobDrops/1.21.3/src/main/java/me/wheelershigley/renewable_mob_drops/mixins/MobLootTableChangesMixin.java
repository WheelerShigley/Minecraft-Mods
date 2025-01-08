package me.wheelershigley.renewable_mob_drops.mixins;

import net.minecraft.data.server.loottable.EntityLootTableGenerator;
import net.minecraft.data.server.loottable.vanilla.VanillaEntityLootTableGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VanillaEntityLootTableGenerator.class)
public abstract class MobLootTableChangesMixin extends EntityLootTableGenerator {
    protected MobLootTableChangesMixin(FeatureSet requiredFeatures, RegistryWrapper.WrapperLookup registries) {
        super(requiredFeatures, registries);
    }
    @Override public void generate() {}

    /*
     * @author Wheeler-Shigley
     * @reason Shulkers drop 1-2 (50%s) shells
     *//*
    @Inject(method = "generate", at = @At("TAIL"))
    public void generate(CallbackInfo ci) {
        LoggerFactory.getLogger("test").info("test");
        //Shulkers drop 1-2 (50%s) shells
        this.register(
            EntityType.SHULKER,
            LootTable.builder().pool(
                LootPool.builder().rolls( ConstantLootNumberProvider.create(1.0F) )
                    .with( ItemEntry.builder(Items.SHULKER_SHELL) )
                    .apply(
                        EnchantedCountIncreaseLootFunction.builder(
                            this.registries,
                            UniformLootNumberProvider.create(1.0F, 2.0F)
                        )
                    )
                .build()
            )
        );
    }*/
}
