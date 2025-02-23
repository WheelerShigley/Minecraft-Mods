package external.LordDeathHunter;

import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

public class registerDrops {
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
