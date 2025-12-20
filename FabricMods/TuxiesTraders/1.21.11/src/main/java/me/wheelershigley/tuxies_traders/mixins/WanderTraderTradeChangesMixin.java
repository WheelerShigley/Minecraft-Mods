package me.wheelershigley.tuxies_traders.mixins;

import me.wheelershigley.tuxies_traders.item.ItemGroups;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.wheelershigley.tuxies_traders.item.ItemHelpers.createPotionStack;

@Mixin(WanderingTraderEntity.class)
public abstract class WanderTraderTradeChangesMixin extends MerchantEntity  {
    public WanderTraderTradeChangesMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * @author Wheeler-Shigley
     * @reason Added Trades
     */
    @Inject(
        method = "fillRecipes",
        at = @At("TAIL")
    )
    protected void fillRecipes(ServerWorld world, CallbackInfo ci) {
        TradeOfferList tradeOfferList = this.getOffers();

        //Custom Trades
        for(Pair<TradeOffers.Factory[], Integer> CUSTOM_TRADER_TRADE : CUSTOM_TRADER_TRADES) {
            TradeOffers.Factory[] factories = CUSTOM_TRADER_TRADE.getKey();
            if(factories != null && 0 < factories.length) {
                //Select single^1 factory
                this.fillRecipesFromPool(
                    world,
                    tradeOfferList,
                    factories,
                    CUSTOM_TRADER_TRADE.getRight()-1 //^1 set of singles
                );
                int i = this.random.nextInt(factories.length-1);
                TradeOffers.Factory factory = factories[i];

                //Create Trade
                TradeOffer tradeOffer = factory.create(world, this, this.random);
                if(tradeOffer != null) {
                    ItemStack drop = null;
                    if(  ItemGroups.eggToDrops.containsKey( tradeOffer.getSellItem().getItem() )  ) {
                        drop = ItemGroups.eggToDrops.get( tradeOffer.getSellItem().getItem() );
                    }
                    if(drop != null) {
                        drop.setCount( drop.getMaxCount() );
                        tradeOffer = new TradeOffer(
                            tradeOffer.getFirstBuyItem(),
                            Optional.of(
                                new TradedItem(
                                    drop.getRegistryEntry(),
                                    drop.getMaxCount(),
                                    ComponentMapPredicate.of( drop.getComponents() )
                                )
                            ),
                            tradeOffer.getSellItem(),
                            tradeOffer.getMaxUses(),
                            tradeOffer.getMerchantExperience(),
                            tradeOffer.getPriceMultiplier()
                        );
                    }

                    tradeOfferList.add(tradeOffer);
                }
            }
        }
    }

    @Unique
    private static Pair<TradeOffers.Factory[], Integer> tradingPair(TradeOffers.Factory[] tradeOfferFactories, Integer Count) {
        return new Pair<>() {
            @Override
            public Integer setValue(Integer value) {
                return null;
            }

            @Override
            public TradeOffers.Factory[] getLeft() {
                return tradeOfferFactories;
            }

            @Override
            public Integer getRight() {
                return Count;
            }
        };
    }

//    @Unique
//    private static final Int2ObjectMap<TradeOffers.Factory[]> CUSTOM_WANDERING_TRADER_TRADES;
    @Unique
    private static final List< Pair<TradeOffers.Factory[], Integer> > CUSTOM_TRADER_TRADES = new ArrayList<>();
    static {
        ArrayList<TradeOffers.Factory> BrewingTrades; {
            BrewingTrades = new ArrayList<>();

            ItemStack[] shortPotions = new ItemStack[]{
                    createPotionStack(Potions.FIRE_RESISTANCE),
                    createPotionStack(Potions.HARMING),
                    createPotionStack(Potions.HEALING),
                    createPotionStack(Potions.INFESTED),
                    createPotionStack(Potions.INVISIBILITY),
                    createPotionStack(Potions.LEAPING),
                    //createPotionStack(Potions.LUCK),
                    createPotionStack(Potions.NIGHT_VISION),
                    createPotionStack(Potions.OOZING),
                    createPotionStack(Potions.POISON),
                    createPotionStack(Potions.REGENERATION),
                    createPotionStack(Potions.SLOWNESS),
                    createPotionStack(Potions.SLOW_FALLING),
                    createPotionStack(Potions.SWIFTNESS),
                    createPotionStack(Potions.TURTLE_MASTER),
                    createPotionStack(Potions.WATER_BREATHING),
                    createPotionStack(Potions.WEAKNESS),
                    createPotionStack(Potions.WEAVING),
                    createPotionStack(Potions.WIND_CHARGED)
            };
            for(ItemStack shortPotion : shortPotions) {
                BrewingTrades.add(
                    new TradeOffers.SellItemFactory(shortPotion, 4, 1, 8, 1)
                );
            }

            BrewingTrades.add(
                new TradeOffers.SellItemFactory(Items.WATER_BUCKET, 1, 1, 16, 2)
            );
            BrewingTrades.add(
                new TradeOffers.SellItemFactory(Items.MILK_BUCKET, 2, 1, 16, 2)
            );
            BrewingTrades.add(
                new TradeOffers.SellItemFactory(Items.SLIME_BALL, 2, 1, 64, 1)
            );
            BrewingTrades.add(
                new TradeOffers.SellItemFactory(Items.GLOWSTONE_DUST, 1, 4, 64, 1)
            );
            BrewingTrades.add(
                new TradeOffers.BuyItemFactory(Items.FERMENTED_SPIDER_EYE, 1, 64, 1, 3)
            );
        }
        CUSTOM_TRADER_TRADES.add(
            tradingPair(
                BrewingTrades.toArray( new TradeOffers.Factory[0] ),
                1
            )
        );

        ArrayList<TradeOffers.Factory> WorldlyTrades; {
            WorldlyTrades = new ArrayList<>();

            //Logs
            Block[] EasyLogs = new Block[]{
                Blocks.BAMBOO_BLOCK
            };
            for(Block EasyLog : EasyLogs) {
                WorldlyTrades.add(
                    new TradeOffers.SellItemFactory(EasyLog, 2, 8, 8, 1)
                );
            }
            Block[] MediumLogs = new Block[]{
                Blocks.ACACIA_LOG,
                Blocks.BIRCH_LOG,
                Blocks.DARK_OAK_LOG,
                Blocks.JUNGLE_LOG,
                Blocks.OAK_LOG,
                Blocks.PALE_OAK_LOG,
                Blocks.SPRUCE_LOG,
                Blocks.CHERRY_LOG
            };
            for(Block MediumLog : MediumLogs) {
                WorldlyTrades.add(
                    new TradeOffers.SellItemFactory(MediumLog, 4, 8, 8, 1)
                );
            }
            Block[] HardLogs = new Block[]{
                Blocks.MANGROVE_LOG
            };
            for(Block HardLog : HardLogs) {
                WorldlyTrades.add(
                    new TradeOffers.SellItemFactory(HardLog, 8, 8, 8, 1)
                );
            }

            //Ices
            WorldlyTrades.add(
                new TradeOffers.SellItemFactory(Items.ICE, 1, 1, 16, 1)
            );
            WorldlyTrades.add(
                new TradeOffers.SellItemFactory(Items.PACKED_ICE, 2, 1, 16, 1)
            );
            WorldlyTrades.add(
                new TradeOffers.SellItemFactory(Items.BLUE_ICE, 4, 1, 16, 1)
            );

            //Miscellaneous Drops
            WorldlyTrades.add(
                new TradeOffers.SellItemFactory(Items.GUNPOWDER, 1, 4, 2, 1)
            );
        }
        CUSTOM_TRADER_TRADES.add(
            tradingPair(
                WorldlyTrades.toArray( new TradeOffers.Factory[0] ),
                1
            )
        );

        ArrayList<TradeOffers.Factory> NonRenewableTrades; {
            NonRenewableTrades = new ArrayList<>();

            Item[] corals = new Item[]{
                Items.BRAIN_CORAL_BLOCK,
                Items.BUBBLE_CORAL_BLOCK,
                Items.FIRE_CORAL_BLOCK,
                Items.HORN_CORAL_BLOCK,
                Items.TUBE_CORAL_BLOCK
            };
            for(Item coral : corals) {
                NonRenewableTrades.add(
                    new TradeOffers.SellItemFactory(coral, 3, 1, 8, 1)
                );
            }

            Item[] dirts = new Item[]{
                Items.ROOTED_DIRT,
                Items.SAND,
                Items.RED_SAND
            };
            for(Item dirt : dirts) {
                new TradeOffers.SellItemFactory(dirt, 1, 1, 256, 1);
            }

            //Miscellaneous
            NonRenewableTrades.add(
                new TradeOffers.SellItemFactory(Items.POINTED_DRIPSTONE, 1, 2, 5, 1)
            );

        }
        CUSTOM_TRADER_TRADES.add(
            tradingPair(
                NonRenewableTrades.toArray( new TradeOffers.Factory[0] ),
                2
            )
        );

        ArrayList<TradeOffers.Factory> AnimalTrades; {
            AnimalTrades = new ArrayList<>();

            //Fish
            AnimalTrades.add(
                new TradeOffers.SellItemFactory(Items.TROPICAL_FISH_BUCKET, 8, 1, 8, 1)
            );
            AnimalTrades.add(
                new TradeOffers.SellItemFactory(Items.PUFFERFISH_BUCKET, 4, 1, 8, 1)
            );
            AnimalTrades.add(
                new TradeOffers.SellItemFactory(Items.COD_BUCKET, 2, 1, 8, 1)
            );
            AnimalTrades.add(
                new TradeOffers.SellItemFactory(Items.SALMON_BUCKET, 2, 1, 8, 1)
            );
            AnimalTrades.add(
                new TradeOffers.SellItemFactory(Items.NAUTILUS_SHELL, 16, 1, 8, 1)
            );
        }
        CUSTOM_TRADER_TRADES.add(
            tradingPair(
                AnimalTrades.toArray( new TradeOffers.Factory[0] ),
                1
            )
        );

        ArrayList<TradeOffers.Factory> PlantTrades; {
            PlantTrades = new ArrayList<>();

            PlantTrades.add(
                new TradeOffers.SellItemFactory(Items.SEA_PICKLE, 2, 1, 5, 1)
            );
            PlantTrades.add(
                new TradeOffers.SellItemFactory(Items.FERN, 1, 1, 12, 1)
            );
            PlantTrades.add(
                new TradeOffers.SellItemFactory(Items.SUGAR_CANE, 1, 1, 8, 1)
            );
            PlantTrades.add(
                new TradeOffers.SellItemFactory(Items.PUMPKIN, 1, 1, 4, 1)
            );
            PlantTrades.add(
                new TradeOffers.SellItemFactory(Items.KELP, 3, 1, 12, 1)
            );
            PlantTrades.add(
                new TradeOffers.SellItemFactory(Items.CACTUS, 3, 1, 8, 1)
            );

            Item[] flowers = new Item[]{
                    Items.DANDELION,
                    Items.POPPY,
                    Items.BLUE_ORCHID,
                    Items.ALLIUM,
                    Items.AZURE_BLUET,
                    Items.RED_TULIP,
                    Items.ORANGE_TULIP,
                    Items.WHITE_TULIP,
                    Items.PINK_TULIP,
                    Items.OXEYE_DAISY,
                    Items.CORNFLOWER,
                    Items.LILY_OF_THE_VALLEY,
                    Items.OPEN_EYEBLOSSOM
            };
            for(Item flower : flowers) {
                PlantTrades.add(
                    new TradeOffers.SellItemFactory(flower, 1, 1, 7, 1)
                );
            }

            Item[] seeds = new Item[]{
                Items.WHEAT_SEEDS,
                Items.BEETROOT_SEEDS,
                Items.PUMPKIN_SEEDS,
                Items.MELON_SEEDS
            };
            for(Item seed : seeds) {
                PlantTrades.add(
                    new TradeOffers.SellItemFactory(seed, 1, 1, 12, 1)
                );
            }

            Item[] saplings = new Item[]{
                    Items.ACACIA_SAPLING,
                    Items.BIRCH_SAPLING,
                    Items.DARK_OAK_SAPLING,
                    Items.JUNGLE_SAPLING,
                    Items.OAK_SAPLING,
                    Items.SPRUCE_SAPLING,
                    Items.CHERRY_SAPLING,
                    Items.PALE_OAK_SAPLING,
                    Items.MANGROVE_PROPAGULE
            };
            for(Item sapling : saplings) {
                PlantTrades.add(
                    new TradeOffers.SellItemFactory(sapling, 5, 1, 8, 1)
                );
            }

            //Miscellaneous
            PlantTrades.add(
                new TradeOffers.SellItemFactory(Items.VINE, 1, 3, 4, 1)
            );
            PlantTrades.add(
                new TradeOffers.SellItemFactory(Items.PALE_HANGING_MOSS, 1, 3, 4, 1)
            );

            PlantTrades.add(
                new TradeOffers.SellItemFactory(Items.BROWN_MUSHROOM, 1, 3, 4, 1)
            );
            PlantTrades.add(
                new TradeOffers.SellItemFactory(Items.RED_MUSHROOM, 1, 3, 4, 1)
            );
            PlantTrades.add(
                new TradeOffers.SellItemFactory(Items.LILY_PAD, 1, 5, 2, 1)
            );
            PlantTrades.add(
                new TradeOffers.SellItemFactory(Items.SMALL_DRIPLEAF, 1, 2, 5, 1)
            );

        }
        CUSTOM_TRADER_TRADES.add(
            tradingPair(
                PlantTrades.toArray( new TradeOffers.Factory[0] ),
                1
            )
        );

        ArrayList<TradeOffers.Factory> ColorTrades; {
            ColorTrades = new ArrayList<>();

            Item[] dyes = new Item[]{
                Items.BLACK_DYE,
                Items.BLUE_DYE,
                Items.BROWN_DYE,
                Items.CYAN_DYE,
                Items.GRAY_DYE,
                Items.GREEN_DYE,
                Items.LIGHT_BLUE_DYE,
                Items.LIGHT_GRAY_DYE,
                Items.LIME_DYE,
                Items.MAGENTA_DYE,
                Items.ORANGE_DYE,
                Items.PINK_DYE,
                Items.PURPLE_DYE,
                Items.RED_DYE,
                Items.WHITE_DYE,
                Items.YELLOW_DYE
            };
            for(Item dye : dyes) {
                ColorTrades.add(
                    new TradeOffers.SellItemFactory(dye, 1, 3, 12, 1)
                );
            }
        }
        CUSTOM_TRADER_TRADES.add(
            tradingPair(
                ColorTrades.toArray( new TradeOffers.Factory[0] ),
                1
            )
        );

        ArrayList<TradeOffers.Factory> PassiveSpawnEggTrades; {
            PassiveSpawnEggTrades = new ArrayList<>();

            for(Item passiveEgg : ItemGroups.passiveMobSpawnEggs) {
                PassiveSpawnEggTrades.add(
                    new TradeOffers.SellItemFactory(passiveEgg, 4, 1, 1, 64)
                );
            }
        }
        CUSTOM_TRADER_TRADES.add(
            tradingPair(
                PassiveSpawnEggTrades.toArray( new TradeOffers.Factory[0] ),
                1
            )
        );

        ArrayList<TradeOffers.Factory> NeutralSpawnEggTrades; {
            NeutralSpawnEggTrades = new ArrayList<>();

            for(Item neutralEgg : ItemGroups.neutralMobSpawnEggs) {
                NeutralSpawnEggTrades.add(
                    new TradeOffers.SellItemFactory(neutralEgg, 6, 1, 1, 64)
                );
            }
        }
        CUSTOM_TRADER_TRADES.add(
            tradingPair(
                NeutralSpawnEggTrades.toArray( new TradeOffers.Factory[0] ),
                1
            )
        );

        ArrayList<TradeOffers.Factory> HostileSpawnEggTrades; {
            HostileSpawnEggTrades = new ArrayList<>();

            for(Item hostileEgg : ItemGroups.hostileMobSpawnEggs) {
                HostileSpawnEggTrades.add(
                    new TradeOffers.SellItemFactory(hostileEgg, 8, 1, 1, 64)
                );
            }
        }
        CUSTOM_TRADER_TRADES.add(
            tradingPair(
                HostileSpawnEggTrades.toArray( new TradeOffers.Factory[0] ),
                1
            )
        );

//        /*CUSTOM_WANDERING_TRADER_TRADES*/ {
//            int accumulator = 1;
//            ImmutableMap<Integer, TradeOffers.Factory[]> map;
//            ImmutableMap.Builder<Integer, TradeOffers.Factory[]> mapBuilder = new ImmutableMap.Builder<>();
//
//            for(Pair<TradeOffers.Factory[], Integer> CUSTOM_TRADER_TRADE : CUSTOM_TRADER_TRADES) {
//                for(int counter = 0; counter < CUSTOM_TRADER_TRADE.getValue(); counter++ ) {
//                    mapBuilder.put(
//                        accumulator++,
//                        CUSTOM_TRADER_TRADE.getKey()
//                    );
//                }
//            }
//            map = mapBuilder.build();
//
//            CUSTOM_WANDERING_TRADER_TRADES = copyToFastUtilMap(map);
//        }
    }
}
