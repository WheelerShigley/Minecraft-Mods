package me.wheelershigley.tradesmaxxing.mixins;

import net.minecraft.item.Item;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.Map;

@Mixin(value = TradeOffers.TypeAwareBuyForOneEmeraldFactory.class, priority = 800)
public class TradeOffersTypeAwareBuyForOneEmeraldFactoryFactoryMixin {
    @Shadow @Final private Map<VillagerType, Item> map;
    @Shadow @Final private int count;
    @Shadow @Final private int maxUses;
    @Shadow @Final private int experience;

    /**
     * @author Wheeler-Shigley
     * @reason Tradesmaxxing
     */
    @Overwrite
    @Nullable
    public TradeOffer create(Entity entity, Random random) {
        if (entity instanceof VillagerDataContainer villagerDataContainer) {
            TradedItem tradedItem = new TradedItem((ItemConvertible)this.map.get(villagerDataContainer.getVillagerData().getType()), this.count);
            return new TradeOffer(tradedItem, new ItemStack(Items.EMERALD), this.maxUses, this.experience, 0.05F);
        } else {
            return null;
        }
    }
}
