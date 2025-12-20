package me.wheelershigley.tradesmaxxing.mixins;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
    value = TradeOffers.SellEnchantedToolFactory.class,
    priority = 800
)
public class SellEnchantedToolFactoryLevelPriceUseMixin {
    @Shadow @Final private int basePrice;
    @Shadow @Final private int maxUses;
    @Shadow @Final private int experience;
    @Shadow @Final private float multiplier;

    @Inject(
        method = "create",
        at = @At("RETURN"),
        cancellable = true
    )
    public void create(
        ServerWorld world,
        Entity entity,
        Random random,
        CallbackInfoReturnable<TradeOffer> cir
    ) {
        TradeOffer originalResult = cir.getReturnValue();

        TradedItem modifiedTradedItem = new TradedItem(Items.EMERALD, this.basePrice);
        TradeOffer modifiedResult = new TradeOffer(
            modifiedTradedItem,
            getEnchantedItemWithMaximumLevels( originalResult.getSellItem() ),
            Integer.MAX_VALUE, //this.maxUses,
            this.experience,
            this.multiplier
        );
        cir.setReturnValue(modifiedResult);
    }

    @Unique
    private ItemStack getEnchantedItemWithMaximumLevels(ItemStack enchantedItem) {
        //Get Maximum Levels Component
        ItemEnchantmentsComponent.Builder maximumEnchantmentComponentBuilder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        ItemEnchantmentsComponent randomEnchantmentComponent = enchantedItem.get(DataComponentTypes.ENCHANTMENTS);
        if(
            randomEnchantmentComponent == null
            || randomEnchantmentComponent.isEmpty()
        ) {
            return enchantedItem;
        }
        for( RegistryEntry<Enchantment> enchantmentEntry : randomEnchantmentComponent.getEnchantments() ) {
            maximumEnchantmentComponentBuilder.add(
                enchantmentEntry,
                enchantmentEntry.value().getMaxLevel()
            );
        }

        //Set Component and return
        ItemStack result = enchantedItem.copy();
        result.set(
            DataComponentTypes.ENCHANTMENTS,
            maximumEnchantmentComponentBuilder.build()
        );
        return result;
    }
}
