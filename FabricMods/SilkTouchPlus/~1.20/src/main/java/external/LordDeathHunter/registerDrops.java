package external.LordDeathHunter;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.wheelershigley.silktouchplus.SilkTouchPlus;
import net.minecraft.component.*;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.ComponentPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class registerDrops {
    public static void registerSilkTouchDrop(LootTable.Builder supplier, Item blockItem) {
        ItemEnchantmentsComponent.Builder component = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        component.set(Enchantments.SILK_TOUCH, 1);
        ComponentMap map = ComponentMap.builder().add(
            DataComponentTypes.ENCHANTMENTS,
            component.build()
        ).build();

        LootPool.Builder builder = LootPool.builder()
            .rolls(
                ConstantLootNumberProvider.create(1)
            )
            .conditionally(
                MatchToolLootCondition.builder(
                    ItemPredicate.Builder.create().component(
                        ComponentPredicate.of(map)
                    )
                )
            )
            .with(
                ItemEntry.builder(blockItem)
            )
            .apply(
                SetCountLootFunction.builder(
                    ConstantLootNumberProvider.create(1f)
                ).build()
            )
        ;

        SilkTouchPlus.LOGGER.warn( map.toString() );
        SilkTouchPlus.LOGGER.info( supplier.toString() );
        supplier.pool( builder.build() );
    }

    public static void registerSilkTouchDropWithNBT(LootTable.Builder supplier, Item blockItem) {
        LootPool.Builder builder = LootPool.builder()
            .rolls( ConstantLootNumberProvider.create(1) )
            .with( ItemEntry.builder(blockItem) )
            /*.apply(
                CopyNbtLootFunction.Builder(ContextLootNbtProvider.BLOCK_ENTITY)
            )*/
            /*.apply(
                CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                    .withOperation("SpawnData", "BlockEntityTag.SpawnData")
                    .withOperation("SpawnPotentials", "BlockEntityTag.SpawnPotentials")
                    .build()
            )*/
            .apply(  SetCountLootFunction.builder( ConstantLootNumberProvider.create(1f) ).build()  )
            ;

        supplier.pool( builder.build() );
    }
}
