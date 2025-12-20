package me.wheelershigley.tradesmaxxing.mixins;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(
    value = TradeOffers.EnchantBookFactory.class,
    priority = 800
)
public class EnchantedBookFactoryLevelPriceUseMixin {
    @Shadow @Final private int experience;

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

        int minimum_cost = 0;
        ItemStack maximumLevelEnchantedBook = new ItemStack(Items.ENCHANTED_BOOK); {
            ItemEnchantmentsComponent.Builder maximumEnchantmentComponentBuilder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);

            ItemStack randomLevelEnchantedBook = originalResult.getSellItem();
            ItemEnchantmentsComponent randomEnchantmentComponent = randomLevelEnchantedBook.get(DataComponentTypes.STORED_ENCHANTMENTS);
            if(
                randomEnchantmentComponent == null
                || randomEnchantmentComponent.isEmpty()
            ) {
                return;
            }
            int level = 0;
            for( RegistryEntry<Enchantment> enchantmentEntry : randomEnchantmentComponent.getEnchantments() ) {
                level = enchantmentEntry.value().getMaxLevel();
                maximumEnchantmentComponentBuilder.add(enchantmentEntry, level);

                //Minimum Cost of Enchantment, by Level
                int cost = 3*level+2;
                if( enchantmentEntry.isIn(EnchantmentTags.DOUBLE_TRADE_PRICE) ) {
                    cost *= 2;
                }
                minimum_cost += cost;
            }

            maximumLevelEnchantedBook.set(
                DataComponentTypes.STORED_ENCHANTMENTS,
                maximumEnchantmentComponentBuilder.build()
            );
        }

        TradeOffer modifiedResult = new TradeOffer(
            new TradedItem(Items.EMERALD, minimum_cost),
            Optional.of( new TradedItem(Items.BOOK) ),
            maximumLevelEnchantedBook,
            Integer.MAX_VALUE, //default of 12
            this.experience,
            0.2F
        );
        cir.setReturnValue(modifiedResult);
    }
}
