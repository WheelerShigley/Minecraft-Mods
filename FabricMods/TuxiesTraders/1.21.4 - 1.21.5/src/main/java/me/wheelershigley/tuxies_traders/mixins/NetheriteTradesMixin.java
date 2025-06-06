package me.wheelershigley.tuxies_traders.mixins;

import me.wheelershigley.tuxies_traders.item.ItemGroups;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(
    value = TradeOffers.SellItemFactory.class,
    priority = 1300
)
public class NetheriteTradesMixin {
    @Shadow @Final private ItemStack sell;
    @Shadow @Final private Optional< RegistryKey<EnchantmentProvider> > enchantmentProviderKey;
    @Shadow @Final private int price;
    @Shadow @Final private int maxUses;
    @Shadow @Final private int experience;
    @Shadow @Final private float multiplier;
    /**
     *
     * @author Wheeler-Shigley
     * @reason Allow for Netherite Trades
     */
    @Overwrite
    public TradeOffer create(Entity entity, Random random) {
        ItemStack itemStack = this.sell.copy();
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
            new TradedItem(
                ItemGroups.isSpawnEgg( sell.getItem() ) ? Items.NETHERITE_INGOT : Items.EMERALD,
                this.price
            ),
            itemStack,
            Integer.MAX_VALUE, //this.maxUses,
            this.experience,
            this.multiplier
        );
    }
}
