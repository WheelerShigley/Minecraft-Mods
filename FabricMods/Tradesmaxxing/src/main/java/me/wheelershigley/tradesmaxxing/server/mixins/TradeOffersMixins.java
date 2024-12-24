package me.wheelershigley.tradesmaxxing.server.mixins;

import com.google.common.collect.Lists;
import me.wheelershigley.tradesmaxxing.server.Tradesmaxxing;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapState;
import net.minecraft.potion.Potion;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.*;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(TradeOffers.class)
public class TradeOffersMixins {
    @Mixin(TradeOffers.BuyItemFactory.class)
    public static class BuyItemFactory {
        @Shadow
        @Final
        private TradedItem stack;
        @Shadow @Final private int experience;
        @Shadow @Final private int price;
        @Shadow @Final private float multiplier;

        /**
         * @author Wheeler-Shigley
         * @reason Tradesmaxxing
         */
        @Overwrite
        public TradeOffer create(Entity entity, Random random) {
            return new TradeOffer(
                    this.stack,
                    new ItemStack( Items.EMERALD, this.price ),
                    Integer.MAX_VALUE,
                    this.experience,
                    this.multiplier
            );
        }
    }

    @Mixin(TradeOffers.EnchantBookFactory.class)
    public static class TradeOffersEnchantBookFactoryMixin {
        @Shadow @Final private int experience;
        @Shadow @Final private TagKey<Enchantment> possibleEnchantments;
        @Shadow @Final private int maxLevel;

        /**
         * @author Wheeler-Shigley
         * @reason Tradesmaxxing
         */
        @Overwrite
        public TradeOffer create(Entity entity, Random random) {
            Optional<RegistryEntry<Enchantment>> optional = entity.getWorld().getRegistryManager()
                    .getOrThrow(RegistryKeys.ENCHANTMENT)
                    .getRandomEntry(this.possibleEnchantments, random)
                    ;
            int level;
            ItemStack itemStack;
            if( !optional.isEmpty() ) {
                RegistryEntry<Enchantment> registryEntry = (RegistryEntry)optional.get();
                Enchantment enchantment = (Enchantment)registryEntry.value();
                level = Math.min(enchantment.getMaxLevel(), this.maxLevel);
                itemStack = EnchantmentHelper.getEnchantedBookWith( new EnchantmentLevelEntry(registryEntry, level) );
                level = 2 + 3*level;
                if( registryEntry.isIn(EnchantmentTags.DOUBLE_TRADE_PRICE) ) {
                    level *= 2;
                }
            } else {
                itemStack = new ItemStack(Items.BOOK);
                level = 1;
            }

            return new TradeOffer(
                    new TradedItem(Items.EMERALD, level),
                    Optional.of( new TradedItem(Items.BOOK) ),
                    itemStack,
                    Integer.MAX_VALUE,
                    this.experience,
                    0.2F
            );
        }
    }

    @Mixin(TradeOffers.ProcessItemFactory.class)
    public static class TradeOffersProcessItemFactoryMixin {
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

    @Mixin(TradeOffers.SellDyedArmorFactory.class)
    public static class TradeOffersSellDyedArmorFactoryMixin {
        @Shadow @Final
        Item sell;
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

    @Mixin(TradeOffers.SellEnchantedToolFactory.class)
    public static class TradeOffersSellEnchantedToolFactoryMixin {

        @Shadow @Final private ItemStack tool;
        @Shadow @Final private int basePrice;
        @Shadow @Final private int experience;
        @Shadow @Final private float multiplier;

        /**
         * @author Wheeler-Shigley
         * @reason Tradesmaxxing
         */
        @Overwrite
        public TradeOffer create(Entity entity, Random random) {
            Tradesmaxxing.LOGGER.info(this.basePrice +" -> "+ (this.basePrice+2) );

            DynamicRegistryManager dynamicRegistryManager = entity.getWorld().getRegistryManager();
            Optional< RegistryEntryList.Named<Enchantment> > optional = dynamicRegistryManager
                    .getOrThrow(RegistryKeys.ENCHANTMENT)
                    .getOptional(EnchantmentTags.ON_TRADED_EQUIPMENT)
                    ;
            ItemStack itemStack = EnchantmentHelper.enchant(
                    random,
                    new ItemStack( this.tool.getItem() ),
                    random.nextInt(15),
                    dynamicRegistryManager,
                    optional
            );

            return new TradeOffer(
                    new TradedItem(Items.EMERALD, this.basePrice+5),
                    itemStack,
                    Integer.MAX_VALUE,
                    this.experience,
                    this.multiplier
            );
        }
    }

    @Mixin(TradeOffers.SellItemFactory.class)
    public static class TradeOffersSellItemFactoryMixin {
        @Shadow @Final private ItemStack sell;
        @Shadow @Final private int price;
        @Shadow @Final private int experience;
        @Shadow @Final private float multiplier;
        @Shadow @Final private Optional< RegistryKey<EnchantmentProvider> > enchantmentProviderKey;

        /**
         * @author Wheeler-Shigley
         * @reason Tradesmaxxing
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
                    new TradedItem(Items.EMERALD, this.price),
                    itemStack,
                    Integer.MAX_VALUE,
                    this.experience,
                    this.multiplier
            );
        }
    }

    @Mixin(TradeOffers.SellMapFactory.class)
    public static class TradeOffersSellMapFactoryMixin {
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

    @Mixin(TradeOffers.SellPotionHoldingItemFactory.class)
    public static class TradeOffersSellPotionHoldingItemFactoryMixin {
        @Shadow @Final private ItemStack sell;
        @Shadow @Final private int sellCount;
        @Shadow @Final private int price;
        @Shadow @Final private int maxUses;
        @Shadow @Final private int experience;
        @Shadow @Final private Item secondBuy;
        @Shadow @Final private int secondCount;
        @Shadow @Final private float priceMultiplier;

        /**
         * @author Wheeler-Shigley
         * @reason Tradesmaxxing
         */
        @Overwrite
        public TradeOffer create(Entity entity, Random random) {
            TradedItem tradedItem = new TradedItem(Items.EMERALD, this.price);
            /*Get a random potion*/
            List< RegistryEntry<Potion> > list = Registries.POTION.streamEntries().filter(
                    (entry) -> {
                        return
                                !( entry.value() ).getEffects().isEmpty()
                                        && entity.getWorld().getBrewingRecipeRegistry().isBrewable(entry)
                                ;
                    }
            ).collect( Collectors.toList() );
            RegistryEntry<Potion> registryEntry = Util.getRandom(list, random);

            ItemStack itemStack = new ItemStack(this.sell.getItem(), this.sellCount);
            itemStack.set( DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(registryEntry) );
            return new TradeOffer(
                    tradedItem,
                    Optional.of( new TradedItem(this.secondBuy, this.secondCount) ),
                    itemStack,
                    Integer.MAX_VALUE,
                    this.experience,
                    this.priceMultiplier
            );
        }
    }

    @Mixin(TradeOffers.SellSuspiciousStewFactory.class)
    public static class TradeOffersSellSuspiciousStewFactoryMixin {
        @Shadow @Final private SuspiciousStewEffectsComponent stewEffects;
        @Shadow @Final private int experience;
        @Shadow @Final private float multiplier;

        /**
         * @author Wheeler-Shigley
         * @reason Tradesmaxxing
         */
        @Overwrite
        @Nullable
        public TradeOffer create(Entity entity, Random random) {
            ItemStack itemStack = new ItemStack(Items.SUSPICIOUS_STEW, 1);
            itemStack.set(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, this.stewEffects);
            return new TradeOffer(
                    new TradedItem(Items.EMERALD),
                    itemStack,
                    Integer.MAX_VALUE,
                    this.experience,
                    this.multiplier
            );
        }
    }

    @Mixin(value = TradeOffers.TypeAwareBuyForOneEmeraldFactory.class, priority = 800)
    public static class TradeOffersTypeAwareBuyForOneEmeraldFactoryFactoryMixin {
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

}
