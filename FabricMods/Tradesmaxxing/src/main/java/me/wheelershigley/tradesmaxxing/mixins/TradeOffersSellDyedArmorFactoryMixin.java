package me.wheelershigley.tradesmaxxing.mixins;

import com.google.common.collect.Lists;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(TradeOffers.SellDyedArmorFactory.class)
public class TradeOffersSellDyedArmorFactoryMixin {
    @Shadow @Final Item sell;
    @Shadow @Final private int price;
    @Shadow @Final private int experience;

    @Shadow
    private static DyeItem getDye(Random random) {
        return null;
    }

    /**
     * @author Wheeler-Shigley
     * @reason Tradesmaxxing
     */
    @Overwrite
    public TradeOffer create(Entity entity, Random random) {
        TradedItem tradedItem = new TradedItem(Items.EMERALD, this.price);
        ItemStack itemStack = new ItemStack(this.sell);
        if( itemStack.isIn(ItemTags.DYEABLE) ) {
            List<DyeItem> list = Lists.newArrayList();
            list.add( getDye(random) );
            if( 0.7F < random.nextFloat() ) { list.add( getDye(random) ); }
            if( 0.8F < random.nextFloat() ) { list.add( getDye(random) ); }

            itemStack = DyedColorComponent.setColor(itemStack, list);
        }

        return new TradeOffer(
            tradedItem,
            itemStack,
            Integer.MAX_VALUE,
            this.experience,
            0.2F
        );
    }
}
