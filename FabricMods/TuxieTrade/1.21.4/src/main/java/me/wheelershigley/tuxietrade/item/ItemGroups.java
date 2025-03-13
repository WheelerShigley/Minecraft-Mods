package me.wheelershigley.tuxietrade.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;

import java.util.HashMap;

import static me.wheelershigley.tuxietrade.item.ItemHelpers.createPotionStack;
import static me.wheelershigley.tuxietrade.item.ItemHelpers.createTippedArrowStack;

public class ItemGroups {
    public static boolean isSpawnEgg(Item item) {
        for(Item hostileEgg : hostileMobSpawnEggs) {
            if( hostileEgg.equals(item) ) { return true; }
        }
        for(Item neutralEgg : neutralMobSpawnEggs) {
            if( neutralEgg.equals(item) ) { return true; }
        }
        for(Item passiveEgg : passiveMobSpawnEggs) {
            if( passiveEgg.equals(item) ) { return true; }
        }

        return false;
    }

    public static final HashMap<Item, ItemStack> eggToDrops = new HashMap<>(); static {
        //Hostile
        eggToDrops.put(Items.BLAZE_SPAWN_EGG,           new ItemStack(Items.BLAZE_ROD)            );
        eggToDrops.put(Items.BOGGED_SPAWN_EGG,          createTippedArrowStack(Potions.POISON)    );
        eggToDrops.put(Items.BREEZE_SPAWN_EGG,          new ItemStack(Items.BREEZE_ROD)            );
        eggToDrops.put(Items.CREAKING_SPAWN_EGG,        new ItemStack(Items.RESIN_CLUMP)           );
        eggToDrops.put(Items.CREEPER_SPAWN_EGG,         new ItemStack(Items.GUNPOWDER)             );
        eggToDrops.put(Items.ELDER_GUARDIAN_SPAWN_EGG,  new ItemStack(Items.SPONGE)                );
//      eggToDrops.put(Items.ENDER_DRAGON_SPAWN_EGG,    new ItemStack(Items.DRAGON_EGG)            );
        eggToDrops.put(Items.ENDERMITE_SPAWN_EGG,       new ItemStack(Items.CHORUS_FRUIT)          );
        eggToDrops.put(Items.EVOKER_SPAWN_EGG,          new ItemStack(Items.TOTEM_OF_UNDYING)      );
        eggToDrops.put(Items.GHAST_SPAWN_EGG,           new ItemStack(Items.GHAST_TEAR)            );
        eggToDrops.put(Items.GUARDIAN_SPAWN_EGG,        new ItemStack(Items.PRISMARINE_SHARD)      );
        eggToDrops.put(Items.HOGLIN_SPAWN_EGG,          new ItemStack(Items.PORKCHOP)              );
        eggToDrops.put(Items.HUSK_SPAWN_EGG,            new ItemStack(Items.SAND)                  );
        eggToDrops.put(Items.MAGMA_CUBE_SPAWN_EGG,      new ItemStack(Items.MAGMA_BLOCK)           );
        eggToDrops.put(Items.PHANTOM_SPAWN_EGG,         new ItemStack(Items.PHANTOM_MEMBRANE)      );
        eggToDrops.put(Items.PIGLIN_BRUTE_SPAWN_EGG,    new ItemStack(Items.GOLD_INGOT)            );
        eggToDrops.put(Items.PILLAGER_SPAWN_EGG,        new ItemStack(Items.EMERALD)               );
        eggToDrops.put(Items.RAVAGER_SPAWN_EGG,         new ItemStack(Items.SADDLE)                );
        eggToDrops.put(Items.SHULKER_SPAWN_EGG,         new ItemStack(Items.SHULKER_SHELL)         );
        eggToDrops.put(Items.SILVERFISH_SPAWN_EGG,      new ItemStack(Items.STONE_BRICKS)          );
        eggToDrops.put(Items.SKELETON_SPAWN_EGG,        new ItemStack(Items.BONE)                  );
        eggToDrops.put(Items.SLIME_SPAWN_EGG,           new ItemStack(Items.SLIME_BLOCK)           );
        eggToDrops.put(Items.STRAY_SPAWN_EGG,           createTippedArrowStack(Potions.SLOWNESS)  );
        eggToDrops.put(Items.VEX_SPAWN_EGG,             createPotionStack(Potions.POISON)         );
        eggToDrops.put(Items.VINDICATOR_SPAWN_EGG,      new ItemStack(Items.EMERALD)               );
        eggToDrops.put(Items.WARDEN_SPAWN_EGG,          new ItemStack(Items.SCULK_CATALYST)        );
        eggToDrops.put(Items.WITCH_SPAWN_EGG,           new ItemStack(Items.REDSTONE)              );
//      eggToDrops.put(Items.WITHER_SPAWN_EGG,          new ItemStack(Items.NETHER_STAR)           );
        eggToDrops.put(Items.WITHER_SKELETON_SPAWN_EGG, new ItemStack(Items.WITHER_SKELETON_SKULL) );
        eggToDrops.put(Items.ZOGLIN_SPAWN_EGG,          new ItemStack(Items.ROTTEN_FLESH)          );
        eggToDrops.put(Items.ZOMBIE_SPAWN_EGG,          new ItemStack(Items.ROTTEN_FLESH)          );
        eggToDrops.put(Items.ZOMBIE_VILLAGER_SPAWN_EGG, new ItemStack(Items.EMERALD_BLOCK)         );

        //Neutral
        eggToDrops.put(Items.BEE_SPAWN_EGG,                 new ItemStack(Items.HONEY_BLOCK)       );
        eggToDrops.put(Items.CAVE_SPIDER_SPAWN_EGG,         new ItemStack(Items.COBWEB)            );
        eggToDrops.put(Items.DOLPHIN_SPAWN_EGG,             new ItemStack(Items.DRIED_KELP_BLOCK)  );
        eggToDrops.put(Items.DROWNED_SPAWN_EGG,             new ItemStack(Items.TRIDENT)           );
        eggToDrops.put(Items.ENDERMAN_SPAWN_EGG,            new ItemStack(Items.ENDER_PEARL)       );
        eggToDrops.put(Items.FOX_SPAWN_EGG,                 new ItemStack(Items.EMERALD)           );
        eggToDrops.put(Items.GOAT_SPAWN_EGG,                new ItemStack(Items.POWDER_SNOW_BUCKET));
        eggToDrops.put(Items.IRON_GOLEM_SPAWN_EGG,          new ItemStack(Items.IRON_BLOCK)        );
        eggToDrops.put(Items.LLAMA_SPAWN_EGG,               new ItemStack(Items.LEAD)              );
        eggToDrops.put(Items.PANDA_SPAWN_EGG,               new ItemStack(Items.BAMBOO)            );
        eggToDrops.put(Items.PIGLIN_SPAWN_EGG,              new ItemStack(Items.CRYING_OBSIDIAN)   );
        eggToDrops.put(Items.POLAR_BEAR_SPAWN_EGG,          new ItemStack(Items.BLUE_ICE)          );
        eggToDrops.put(Items.SPIDER_SPAWN_EGG,              new ItemStack(Items.STRING)            );
        eggToDrops.put(Items.TRADER_LLAMA_SPAWN_EGG,        new ItemStack(Items.LEAD)              );
        eggToDrops.put(Items.WOLF_SPAWN_EGG,                new ItemStack(Items.BONE)              );
        eggToDrops.put(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG,    new ItemStack(Items.ROTTEN_FLESH)      );

        //Passive
        eggToDrops.put(Items.ALLAY_SPAWN_EGG,               createPotionStack(Potions.REGENERATION) );
        eggToDrops.put(Items.ARMADILLO_SPAWN_EGG,           new ItemStack(Items.ARMADILLO_SCUTE)       );
        eggToDrops.put(Items.AXOLOTL_SPAWN_EGG,             new ItemStack(Items.GLOW_BERRIES)          );
        eggToDrops.put(Items.BAT_SPAWN_EGG,                 new ItemStack(Items.STONE)                 );
        eggToDrops.put(Items.CAMEL_SPAWN_EGG,               new ItemStack(Items.CACTUS)                );
        eggToDrops.put(Items.CAT_SPAWN_EGG,                 new ItemStack(Items.STRING)                );
        eggToDrops.put(Items.CHICKEN_SPAWN_EGG,             new ItemStack(Items.FEATHER)               );
        eggToDrops.put(Items.COD_SPAWN_EGG,                 new ItemStack(Items.COD)                   );
        eggToDrops.put(Items.COW_SPAWN_EGG,                 new ItemStack(Items.LEATHER)               );
        eggToDrops.put(Items.DONKEY_SPAWN_EGG,              new ItemStack(Items.LEATHER)               );
        eggToDrops.put(Items.FROG_SPAWN_EGG,                new ItemStack(Items.PEARLESCENT_FROGLIGHT) );
        eggToDrops.put(Items.GLOW_SQUID_SPAWN_EGG,          new ItemStack(Items.GLOW_INK_SAC)          );
        eggToDrops.put(Items.HORSE_SPAWN_EGG,               new ItemStack(Items.LEATHER)               );
        eggToDrops.put(Items.MOOSHROOM_SPAWN_EGG,           new ItemStack(Items.RED_MUSHROOM)          );
        eggToDrops.put(Items.MULE_SPAWN_EGG,                new ItemStack(Items.LEATHER)               );
        eggToDrops.put(Items.OCELOT_SPAWN_EGG,              new ItemStack(Items.COCOA_BEANS)           );
        eggToDrops.put(Items.PARROT_SPAWN_EGG,              new ItemStack(Items.VINE)                  );
        eggToDrops.put(Items.PIG_SPAWN_EGG,                 new ItemStack(Items.PORKCHOP)              );
        eggToDrops.put(Items.PUFFERFISH_SPAWN_EGG,          new ItemStack(Items.PUFFERFISH)            );
        eggToDrops.put(Items.RABBIT_SPAWN_EGG,              new ItemStack(Items.RABBIT_FOOT)           );
        eggToDrops.put(Items.SALMON_SPAWN_EGG,              new ItemStack(Items.SALMON)                );
        eggToDrops.put(Items.SHEEP_SPAWN_EGG,               new ItemStack(Items.WHITE_WOOL)            );
        eggToDrops.put(Items.SKELETON_HORSE_SPAWN_EGG,      new ItemStack(Items.BONE)                  );
        eggToDrops.put(Items.SNIFFER_SPAWN_EGG,             new ItemStack(Items.TORCHFLOWER_SEEDS)     );
        eggToDrops.put(Items.SNOW_GOLEM_SPAWN_EGG,          new ItemStack(Items.SNOW_BLOCK)            );
        eggToDrops.put(Items.SQUID_SPAWN_EGG,               new ItemStack(Items.INK_SAC)               );
        eggToDrops.put(Items.STRIDER_SPAWN_EGG,             new ItemStack(Items.STRING)                );
        eggToDrops.put(Items.TADPOLE_SPAWN_EGG,             new ItemStack(Items.MUD)                   );
        eggToDrops.put(Items.TROPICAL_FISH_SPAWN_EGG,       new ItemStack(Items.TROPICAL_FISH)         );
        eggToDrops.put(Items.TURTLE_SPAWN_EGG,              new ItemStack(Items.TURTLE_EGG)            );
        eggToDrops.put(Items.VILLAGER_SPAWN_EGG,            new ItemStack(Items.EMERALD_BLOCK)         );
//      eggToDrops.put(Items.WANDERING_TRADER_SPAWN_EGG,    new ItemStack(Items.NETHERITE_BLOCK)       );
        eggToDrops.put(Items.ZOMBIE_HORSE_SPAWN_EGG,        new ItemStack(Items.ROTTEN_FLESH)          );

    }

    public static final Item[] hostileMobSpawnEggs = new Item[]{
            Items.BLAZE_SPAWN_EGG,
            Items.BOGGED_SPAWN_EGG,
            Items.BREEZE_SPAWN_EGG,
            Items.CREAKING_SPAWN_EGG,
            Items.CREEPER_SPAWN_EGG,
            Items.ELDER_GUARDIAN_SPAWN_EGG,
//          Items.ENDER_DRAGON_SPAWN_EGG,
            Items.ENDERMITE_SPAWN_EGG,
            Items.EVOKER_SPAWN_EGG,
            Items.GHAST_SPAWN_EGG,
            Items.GUARDIAN_SPAWN_EGG,
            Items.HOGLIN_SPAWN_EGG,
            Items.HUSK_SPAWN_EGG,
            Items.MAGMA_CUBE_SPAWN_EGG,
            Items.PHANTOM_SPAWN_EGG,
            Items.PIGLIN_BRUTE_SPAWN_EGG,
            Items.PILLAGER_SPAWN_EGG,
            Items.RAVAGER_SPAWN_EGG,
            Items.SHULKER_SPAWN_EGG,
            Items.SILVERFISH_SPAWN_EGG,
            Items.SKELETON_SPAWN_EGG,
            Items.SLIME_SPAWN_EGG,
            Items.STRAY_SPAWN_EGG,
            Items.VEX_SPAWN_EGG,
            Items.VINDICATOR_SPAWN_EGG,
            Items.WARDEN_SPAWN_EGG,
            Items.WITCH_SPAWN_EGG,
//          Items.WITHER_SPAWN_EGG,
            Items.WITHER_SKELETON_SPAWN_EGG,
            Items.ZOGLIN_SPAWN_EGG,
            Items.ZOMBIE_SPAWN_EGG,
            Items.ZOMBIE_VILLAGER_SPAWN_EGG
    };

    public static final Item[] neutralMobSpawnEggs = new Item[]{
            Items.BEE_SPAWN_EGG,
            Items.CAVE_SPIDER_SPAWN_EGG,
            Items.DOLPHIN_SPAWN_EGG,
            Items.DROWNED_SPAWN_EGG,
            Items.ENDERMAN_SPAWN_EGG,
            Items.FOX_SPAWN_EGG,
            Items.GOAT_SPAWN_EGG,
            Items.IRON_GOLEM_SPAWN_EGG,
            Items.LLAMA_SPAWN_EGG,
            Items.PANDA_SPAWN_EGG,
            Items.PIGLIN_SPAWN_EGG,
            Items.POLAR_BEAR_SPAWN_EGG,
            Items.SPIDER_SPAWN_EGG,
            Items.TRADER_LLAMA_SPAWN_EGG,
            Items.WOLF_SPAWN_EGG,
            Items.ZOMBIFIED_PIGLIN_SPAWN_EGG
    };

    public static final Item[] passiveMobSpawnEggs = new Item[]{
            Items.ALLAY_SPAWN_EGG,
            Items.ARMADILLO_SPAWN_EGG,
            Items.AXOLOTL_SPAWN_EGG,
            Items.BAT_SPAWN_EGG,
            Items.CAMEL_SPAWN_EGG,
            Items.CAT_SPAWN_EGG,
            Items.CHICKEN_SPAWN_EGG,
            Items.COD_SPAWN_EGG,
            Items.COW_SPAWN_EGG,
            Items.DONKEY_SPAWN_EGG,
            Items.FROG_SPAWN_EGG,
            Items.GLOW_SQUID_SPAWN_EGG,
            Items.HORSE_SPAWN_EGG,
            Items.MOOSHROOM_SPAWN_EGG,
            Items.MULE_SPAWN_EGG,
            Items.OCELOT_SPAWN_EGG,
            Items.PARROT_SPAWN_EGG,
            Items.PIG_SPAWN_EGG,
            Items.PUFFERFISH_SPAWN_EGG,
            Items.RABBIT_SPAWN_EGG,
            Items.SALMON_SPAWN_EGG,
            Items.SHEEP_SPAWN_EGG,
            Items.SKELETON_HORSE_SPAWN_EGG,
            Items.SNIFFER_SPAWN_EGG,
            Items.SNOW_GOLEM_SPAWN_EGG,
            Items.SQUID_SPAWN_EGG,
            Items.STRIDER_SPAWN_EGG,
            Items.TADPOLE_SPAWN_EGG,
            Items.TROPICAL_FISH_SPAWN_EGG,
            Items.TURTLE_SPAWN_EGG,
            Items.VILLAGER_SPAWN_EGG,
//          Items.WANDERING_TRADER_SPAWN_EGG,
            Items.ZOMBIE_HORSE_SPAWN_EGG
    };
}
