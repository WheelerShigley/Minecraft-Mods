package me.wheelershigley.tradesmaxxing.mixins;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(TradeOffers.SellMapFactory.class)
public class TradeOffersSellMapFactoryMixin {
    @Shadow @Final int price;
    @Shadow @Final private TagKey<Structure> structure;
    @Shadow @Final private String nameKey;
    @Shadow @Final private RegistryEntry<MapDecorationType> decoration;
    @Shadow @Final private int experience;

    /**
     * @author Wheeler-Shigley
     * @reason Tradesmaxxing
     */
    @Overwrite
    @Nullable
    public TradeOffer create(Entity entity, Random random) {
        if( !(entity.getWorld() instanceof ServerWorld) ) { return null; }

        ServerWorld serverWorld = (ServerWorld)entity.getWorld();
        BlockPos blockPos = serverWorld.locateStructure(this.structure, entity.getBlockPos(), 100, true);
        if(blockPos == null) { return null; }

        ItemStack itemStack = FilledMapItem.createMap(
            serverWorld,
            blockPos.getX(),
            blockPos.getZ(),
            (byte)2,
            true,
            true
        );
        FilledMapItem.fillExplorationMap(serverWorld, itemStack);
        MapState.addDecorationsNbt(itemStack, blockPos, "+", this.decoration);
        itemStack.set( DataComponentTypes.ITEM_NAME, Text.translatable(this.nameKey) );
        return new TradeOffer(
            new TradedItem(Items.EMERALD, this.price),
            Optional.of( new TradedItem(Items.COMPASS) ),
            itemStack,
            Integer.MAX_VALUE,
            this.experience,
            0.2F
        );
    }
}