package me.wheelershigley.tradesmaxxing.mixins;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(TradeOffers.ProcessItemFactory.class)
public class TradeOffersProcessItemFactoryMixin {
    @Shadow @Final private TradedItem toBeProcessed;
    @Shadow @Final private int price;
    @Shadow @Final private ItemStack processed;
    @Shadow @Final private int experience;
    @Shadow @Final private float multiplier;
    @Shadow @Final private Optional<RegistryKey<EnchantmentProvider>> enchantmentProviderKey;

    /**
     * @author Wheeler-Shigley
     * @reason Tradesmaxxing
     */
    @Overwrite
    @Nullable
    public TradeOffer create(Entity entity, Random random) {
        ItemStack itemStack = this.processed.copy();
        World world = entity.getWorld();
        this.enchantmentProviderKey.ifPresent(
            (key) -> {
                EnchantmentHelper.applyEnchantmentProvider(
                    itemStack,
                    world.getRegistryManager(),
                    key,
                    world.getLocalDifficulty( entity.getBlockPos() ),
                    random
                );
            }
        );
        return new TradeOffer(
            new TradedItem(Items.EMERALD, this.price),
            Optional.of(this.toBeProcessed),
            itemStack,
            0,
            Integer.MAX_VALUE,
            this.experience,
            this.multiplier
        );
    }
}