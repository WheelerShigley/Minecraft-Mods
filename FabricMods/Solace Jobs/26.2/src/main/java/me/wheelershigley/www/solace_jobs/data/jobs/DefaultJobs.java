package me.wheelershigley.www.solace_jobs.data.jobs;

import me.wheelershigley.www.dandelion.registerations.ModItems;
import me.wheelershigley.www.solace_jobs.IdentifierHelper;
import me.wheelershigley.www.solace_jobs.SolaceJobs;
import me.wheelershigley.www.solace_jobs.rewards.ActionReward;
import me.wheelershigley.www.solace_jobs.rewards.ActionType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static me.wheelershigley.www.solace_jobs.IdentifierHelper.*;

public class DefaultJobs {
    //TODO: fix items to be identifiers
    public static final JobDefinition HUNTER; static {
        boolean dandelionIsLoaded = FabricLoader.getInstance().isModLoaded("dandelion");
        HUNTER = new JobDefinition(
            solaceJobsIdentifier("hunter"),
            identifierOf(dandelionIsLoaded ? ModItems.GREEN_SWORD_ICON : Items.DIAMOND_SWORD),
            identifierOf(dandelionIsLoaded ? ModItems.RED_SWORD_ICON   : Items.WOODEN_SWORD ),
            new LinkedHashSet<>()
        );

        HUNTER.addReward(
            getIdentifiedReward(
                "common",
                Set.of(
                    EntityTypes.ZOMBIE, EntityTypes.ZOMBIE_VILLAGER, EntityTypes.ZOMBIE_HORSE,
                    EntityTypes.HUSK, EntityTypes.CAMEL_HUSK, EntityTypes.PARCHED,
                    EntityTypes.SKELETON, EntityTypes.BOGGED, EntityTypes.STRAY, EntityTypes.SKELETON_HORSE,
                    EntityTypes.SPIDER, EntityTypes.CAVE_SPIDER, EntityTypes.SILVERFISH,
                    EntityTypes.SLIME, EntityTypes.SULFUR_CUBE, EntityTypes.MAGMA_CUBE,
                    EntityTypes.CREEPER, EntityTypes.PHANTOM,
                    EntityTypes.BLAZE, EntityTypes.BREEZE,
                    EntityTypes.GHAST, EntityTypes.HOGLIN, EntityTypes.ZOGLIN,
                    EntityTypes.PIGLIN, EntityTypes.PIGLIN_BRUTE, EntityTypes.ZOMBIFIED_PIGLIN,
                    EntityTypes.WITHER_SKELETON,
                    EntityTypes.ENDERMAN, EntityTypes.ENDERMITE, EntityTypes.SHULKER
                ),
                ActionType.KILL,
                0, 7.4, 0.008
            )
        );
        HUNTER.addReward(
            getIdentifiedReward(
                "golem",
                Set.of(
                    EntityTypes.COPPER_GOLEM, EntityTypes.IRON_GOLEM, EntityTypes.SNOW_GOLEM,
                    EntityTypes.VILLAGER, EntityTypes.WANDERING_TRADER,
                    EntityTypes.ALLAY, EntityTypes.HAPPY_GHAST
                ),
                ActionType.KILL,
                0, 0.5, 0
            )
        );
        HUNTER.addReward(
            getIdentifiedReward(
                "raid",
                Set.of(
                    EntityTypes.WITCH,
                    EntityTypes.EVOKER, EntityTypes.PILLAGER,
                    EntityTypes.RAVAGER, EntityTypes.VEX, EntityTypes.VINDICATOR
                ),
                ActionType.KILL,
                0, 3, 0.05
            )
        );
        HUNTER.addReward(
            getIdentifiedReward(
                "boss",
                Set.of(
                    EntityTypes.WARDEN, EntityTypes.WITHER, EntityTypes.ENDER_DRAGON
                ),
                ActionType.KILL,
                7, 140, 0.2
            )
        );
    }

    public static final JobDefinition MINER; static {
        boolean dandelionIsLoaded = FabricLoader.getInstance().isModLoaded("dandelion");
        MINER = new JobDefinition(
            solaceJobsIdentifier("miner"),
            identifierOf(dandelionIsLoaded ? ModItems.GREEN_PICKAXE_ICON : Items.DIAMOND_PICKAXE),
            identifierOf(dandelionIsLoaded ? ModItems.RED_PICKAXE_ICON   : Items.WOODEN_PICKAXE ),
            new LinkedHashSet<>()
        );

        MINER.addReward(
            getIdentifiedBreakReward(
                "common_ores",
                Set.of(
                    Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.COPPER_ORE, Blocks.GOLD_ORE,
                    Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE
                ),
                0, 10, 0.06
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "uncommon_ores",
                Set.of(
                    Blocks.REDSTONE_ORE, Blocks.EMERALD_ORE, Blocks.LAPIS_ORE, Blocks.GOLD_ORE,
                    Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_COPPER_ORE
                ),
                0, 20, 0.12
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "rare_ores",
                Set.of(
                    Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.ANCIENT_DEBRIS
                ),
                0, 40, 0.24
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "very_rare_ores",
                Set.of(
                    Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DIAMOND_ORE
                ),
                0, 80, 0.48
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "simple_stones",
                Set.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE),
                0, 0.1, 0.001
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "uncommon_stones",
                Set.of(
                    Blocks.CALCITE, Blocks.SMOOTH_BASALT,
                    Blocks.TUFF, Blocks.OBSIDIAN,
                    Blocks.DRIPSTONE_BLOCK, Blocks.POINTED_DRIPSTONE,
                    Blocks.CINNABAR, Blocks.SULFUR, Blocks.POTENT_SULFUR, Blocks.SULFUR_SPIKE
                ),
                0.03, 0.3, 0.002
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "nether_stones",
                Set.of(
                    Blocks.NETHERRACK, Blocks.BLACKSTONE, Blocks.MAGMA_BLOCK, Blocks.BASALT,
                    Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM
                ),
                0.0004, 0.04, 0.0006
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "end_stones",
                Set.of(Blocks.END_STONE),
                0.015, 2, 0.01
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "sandstones",
                Set.of(Blocks.SANDSTONE, Blocks.RED_SANDSTONE),
                0.02, 0.7, 0.002
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "ices",
                Set.of(Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE),
                0.002, 0.3, 0.001
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "corals",
                Set.of(Blocks.TUBE_CORAL, Blocks.BRAIN_CORAL, Blocks.BUBBLE_CORAL, Blocks.FIRE_CORAL, Blocks.HORN_CORAL),
                0.05, 1.2, 0.01
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "amethysts",
                Set.of(
                    Blocks.AMETHYST_BLOCK, Blocks.BUDDING_AMETHYST,
                    Blocks.SMALL_AMETHYST_BUD, Blocks.MEDIUM_AMETHYST_BUD, Blocks.LARGE_AMETHYST_BUD, Blocks.AMETHYST_CLUSTER
                ),
                0.04, 0.4, 0.005
            )
        );
    }

    public static final JobDefinition LUMBERJACK; static {
        boolean dandelionIsLoaded = FabricLoader.getInstance().isModLoaded("dandelion");
        LUMBERJACK = new JobDefinition(
            solaceJobsIdentifier("lumberjack"),
            identifierOf(dandelionIsLoaded ? ModItems.GREEN_AXE_ICON : Items.DIAMOND_AXE),
            identifierOf(dandelionIsLoaded ? ModItems.RED_AXE_ICON   : Items.WOODEN_AXE ),
            new LinkedHashSet<>()
        );

        LUMBERJACK.addReward(
            getIdentifiedBreakReward(
                "logs",
                Set.of(
                    Blocks.OAK_LOG, Blocks.BIRCH_LOG, Blocks.SPRUCE_LOG, Blocks.JUNGLE_LOG,
                    Blocks.DARK_OAK_LOG, Blocks.ACACIA_LOG,
                    Blocks.MANGROVE_LOG, Blocks.CHERRY_LOG, Blocks.PALE_OAK_LOG
                ),
                0.004, 1.3, 0.013
            )
        );
    }

    public static final JobDefinition EXCAVATOR; static {
        boolean dandelionIsLoaded = FabricLoader.getInstance().isModLoaded("dandelion");
        EXCAVATOR = new JobDefinition(
            solaceJobsIdentifier("excavator"),
            identifierOf(dandelionIsLoaded ? ModItems.GREEN_SHOVEL_ICON : Items.DIAMOND_SHOVEL),
            identifierOf(dandelionIsLoaded ? ModItems.RED_SHOVEL_ICON   : Items.WOODEN_SHOVEL ),
            new LinkedHashSet<>()
        );

        EXCAVATOR.addReward(
            getIdentifiedBreakReward(
                "grasses",
                Set.of(Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.PODZOL),
                0.02, 0.16, 0.0016
            )
        );
        EXCAVATOR.addReward(
            getIdentifiedBreakReward(
                "dirts",
                Set.of(Blocks.DIRT, Blocks.DIRT_PATH, Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.FARMLAND),
                0.01, 0.25, 0.00264
            )
        );
        EXCAVATOR.addReward(
            getIdentifiedBreakReward(
                "clays",
                Set.of(Blocks.CLAY, Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS),
                0.04, 2, 0.005
            )
        );
        EXCAVATOR.addReward(
            getIdentifiedBreakReward(
                "sands",
                Set.of(Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL),
                0.01, 5, 0.00245
            )
        );
        EXCAVATOR.addReward(
            getIdentifiedBreakReward(
                "snow",
                Set.of(Blocks.SNOW, Blocks.SNOW_BLOCK),
                0.002, 0.2, 0.0001
            )
        );
        EXCAVATOR.addReward(
            getIdentifiedBreakReward(
                "souls",
                Set.of(Blocks.SOUL_SAND, Blocks.SOUL_SOIL),
                0.1, 2.5, 0.0015
            )
        );
    }

    public static final JobDefinition FARMER; static {
        boolean dandelionIsLoaded = FabricLoader.getInstance().isModLoaded("dandelion");
        FARMER = new JobDefinition(
            solaceJobsIdentifier("farmer"),
            identifierOf(dandelionIsLoaded ? ModItems.GREEN_HOE_ICON : Items.DIAMOND_HOE),
            identifierOf(dandelionIsLoaded ? ModItems.RED_HOE_ICON   : Items.WOODEN_HOE ),
            new LinkedHashSet<>()
        );

        FARMER.addReward(
            getIdentifiedBreakReward(
                "harvest",
                Set.of(
                    Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS,
                    Blocks.SUGAR_CANE, Blocks.CACTUS
                ),
                0.2, 9, 0.01
            )
        );
        FARMER.addReward(
            getIdentifiedBreakReward(
                "stemmed_harvest",
                Set.of(Blocks.PUMPKIN, Blocks.MELON),
                0.2, 4, 0.004
            )
        );
        FARMER.addReward(
            getIdentifiedReward(
                "disinfection",
                Set.of(
                    EntityTypes.MOOSHROOM
                ),
                ActionType.SHEAR,
                2.2, 26.7, 2.6666
            )
        );

        Set<Object> farmlikeAnimals = Set.of(
            EntityTypes.COW, EntityTypes.SHEEP, EntityTypes.CHICKEN, EntityTypes.PIG, EntityTypes.MOOSHROOM,
            EntityTypes.HORSE, EntityTypes.MULE, EntityTypes.DONKEY, EntityTypes.CAMEL,
            EntityTypes.LLAMA, EntityTypes.TRADER_LLAMA, EntityTypes.GOAT,
            EntityTypes.WOLF, EntityTypes.CAT, EntityTypes.OCELOT,
            EntityTypes.PANDA, EntityTypes.POLAR_BEAR,
            EntityTypes.RABBIT, EntityTypes.FOX, EntityTypes.ARMADILLO, EntityTypes.SNIFFER,
            EntityTypes.TURTLE, EntityTypes.AXOLOTL, EntityTypes.FROG, EntityTypes.TADPOLE,
            EntityTypes.STRIDER
        );
        FARMER.addReward(
            getIdentifiedReward(
                "husbandry",
                farmlikeAnimals,
                ActionType.BREED,
                0, 20, 0.05
            )
        );
        FARMER.addReward(
            getIdentifiedReward(
                "butchery",
                farmlikeAnimals,
                ActionType.KILL,
                0, 8, 0.088
            )
        );
    }

    public static final JobDefinition FISHER; static {
        boolean dandelionIsLoaded = FabricLoader.getInstance().isModLoaded("dandelion");
        FISHER = new JobDefinition(
            solaceJobsIdentifier("fisher"),
            identifierOf(dandelionIsLoaded ? ModItems.GREEN_FISHING_ROD_ICON : Items.BREEZE_ROD),
            identifierOf(dandelionIsLoaded ? ModItems.RED_FISHING_ROD_ICON   : Items.STICK     ),
            new LinkedHashSet<>()
        );

        FISHER.addReward(
            getIdentifiedReward(
                "fishes",
                Set.of(
                    Items.COD, Items.SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH,
                    Items.NAUTILUS_SHELL
                ),
                ActionType.FISH,
                0, 2.4, 0.023
            )
        );
        FISHER.addReward(
            getIdentifiedReward(
                "manual",
                Set.of(
                    EntityTypes.COD, EntityTypes.SALMON, EntityTypes.PUFFERFISH, EntityTypes.TROPICAL_FISH,
                    EntityTypes.DOLPHIN, EntityTypes.SQUID, EntityTypes.GLOW_SQUID,
                    EntityTypes.NAUTILUS, EntityTypes.ZOMBIE_NAUTILUS
                ),
                ActionType.KILL,
                0, 6, 0.088
            )
        );
        FISHER.addReward(
            getIdentifiedReward(
                "defense",
                Set.of(
                    EntityTypes.GUARDIAN, EntityTypes.ELDER_GUARDIAN,
                    EntityTypes.DROWNED
                ),
                ActionType.KILL,
                0, 0.2, 0.005
            )
        );
    }

    public static final JobDefinition ARCHEOLOGIST; static {
        boolean dandelionIsLoaded = FabricLoader.getInstance().isModLoaded("dandelion");
        ARCHEOLOGIST = new JobDefinition(
            solaceJobsIdentifier("archeologist"),
            identifierOf(dandelionIsLoaded ? ModItems.GREEN_BRUSH_ICON : Items.BONE ),
            identifierOf(dandelionIsLoaded ? ModItems.RED_BRUSH_ICON   : Items.STICK),
            new LinkedHashSet<>()
        );

        ARCHEOLOGIST.addReward(
            getIdentifiedReward(
                "brushables",
                Set.of(
                    Blocks.SUSPICIOUS_GRAVEL, Blocks.SUSPICIOUS_SAND
                ),
                ActionType.BRUSH,
                1.2, 6, 0.07
            )
        );
    }

    private static IdentifiableReward getIdentifiedBreakReward(
        String name,
        Set<Object> set,
        double experience, double job_experience, double currency
    ) {
        return new IdentifiableReward(
            new IdentifiableSource<>(
                IdentifierHelper.solaceJobsIdentifier(name),
                getIdentifiers(set)
            ),
            new ActionReward(experience, job_experience, currency)
        );
    }
    private static IdentifiableReward getIdentifiedReward(
            String name,
            Set<Object> set,
            ActionType actionType,
            double experience, double job_experience, double currency
    ) {
        return new IdentifiableReward(
            new IdentifiableSource<>(
                IdentifierHelper.solaceJobsIdentifier(name),
                actionType,
                getIdentifiers(set)
            ),
            new ActionReward(experience, job_experience, currency)
        );
    }
    private static Set<Identifier> getIdentifiers(Set<Object> set) {
        Set<Identifier> result = new HashSet<>();
        for(Object element : set) {
            if(element instanceof Block) {
                result.add(
                    BuiltInRegistries.BLOCK.getKey( (Block)element )
                );
            }
            if(element instanceof Item) {
                result.add(
                    BuiltInRegistries.ITEM.getKey( (Item)element )
                );
            }
            if(element instanceof EntityType<?>) {
                result.add(
                    BuiltInRegistries.ENTITY_TYPE.getKey( (EntityType<?>)element )
                );
            }
        }
        return result;
    }
}
