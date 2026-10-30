package me.wheelershigley.www.solace_jobs.data.jobs;

import me.wheelershigley.www.solace_jobs.SolaceJobs;
import me.wheelershigley.www.solace_jobs.data.ActionReward;
import me.wheelershigley.www.solace_jobs.data.ActionType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.Set;

import static me.wheelershigley.www.solace_jobs.SolaceJobs.solaceJobsIdentifier;

public class DefaultJobs {
    public static final JobDefinition EXCAVATOR; static {
        EXCAVATOR = new JobDefinition(
            solaceJobsIdentifier("excavator"),
            Items.DIAMOND_SHOVEL, Items.WOODEN_SHOVEL,
            new HashSet<>()
        );

        EXCAVATOR.addReward(
            getIdentifiedBreakReward(
                "grasses",
                Set.of(Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.PODZOL),
                0.02, 0.015, 0.0025
            )
        );
        EXCAVATOR.addReward(
            getIdentifiedBreakReward(
                "dirts",
                Set.of(Blocks.DIRT, Blocks.DIRT_PATH, Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.FARMLAND),
                0.01, 0.01, 0.002
            )
        );
        EXCAVATOR.addReward(
            getIdentifiedBreakReward(
                "clays",
                Set.of(Blocks.CLAY, Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS),
                0.04, 0.02, 0.005
            )
        );
        EXCAVATOR.addReward(
            getIdentifiedBreakReward(
                "sands",
                Set.of(Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL),
                0.01, 0.05, 0.001
            )
        );
        EXCAVATOR.addReward(
            getIdentifiedBreakReward(
                "snow",
                Set.of(Blocks.SNOW, Blocks.SNOW_BLOCK),
                0.002, 0.002, 0.0001
            )
        );
        EXCAVATOR.addReward(
            getIdentifiedBreakReward(
                "souls",
                Set.of(Blocks.SOUL_SAND, Blocks.SOUL_SOIL),
                0.1, 0.025, 0.0015
            )
        );
    }

    public static final JobDefinition MINER; static {
        MINER = new JobDefinition(
            solaceJobsIdentifier("miner"),
            Items.DIAMOND_PICKAXE, Items.WOODEN_PICKAXE,
            new HashSet<>()
        );

        MINER.addReward(
            getIdentifiedBreakReward(
                "common_ores",
                Set.of(
                    Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.COPPER_ORE, Blocks.GOLD_ORE,
                    Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE
                ),
                0, 0.1, 0.06
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "uncommon_ores",
                Set.of(
                    Blocks.REDSTONE_ORE, Blocks.EMERALD_ORE, Blocks.LAPIS_ORE, Blocks.GOLD_ORE,
                    Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_COPPER_ORE
                ),
                0, 0.2, 0.12
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "rare_ores",
                Set.of(
                    Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.ANCIENT_DEBRIS
                ),
                0, 0.4, 0.24
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "very_rare_ores",
                Set.of(
                    Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DIAMOND_ORE
                ),
                0, 0.8, 0.48
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "simple_stones",
                Set.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE),
                0, 0.001, 0.001
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
                0.03, 0.003, 0.002
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "nether_stones",
                Set.of(
                    Blocks.NETHERRACK, Blocks.BLACKSTONE, Blocks.MAGMA_BLOCK, Blocks.BASALT,
                    Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM
                ),
                0.0004, 0.0004, 0.0006
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "end_stones",
                Set.of(Blocks.END_STONE),
                0.015, 0.02, 0.01
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "sandstones",
                Set.of(Blocks.SANDSTONE, Blocks.RED_SANDSTONE),
                0.02, 0.007, 0.002
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "ices",
                Set.of(Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE),
                0.002, 0.003, 0.001
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "corals",
                Set.of(Blocks.TUBE_CORAL, Blocks.BRAIN_CORAL, Blocks.BUBBLE_CORAL, Blocks.FIRE_CORAL, Blocks.HORN_CORAL),
                0.05, 0.012, 0.01
            )
        );
        MINER.addReward(
            getIdentifiedBreakReward(
                "amethysts",
                Set.of(
                    Blocks.AMETHYST_BLOCK, Blocks.BUDDING_AMETHYST,
                    Blocks.SMALL_AMETHYST_BUD, Blocks.MEDIUM_AMETHYST_BUD, Blocks.LARGE_AMETHYST_BUD, Blocks.AMETHYST_CLUSTER
                ),
                0.04, 0.004, 0.005
            )
        );
    }

    public static final JobDefinition LUMBERJACK; static {
        LUMBERJACK = new JobDefinition(
            SolaceJobs.solaceJobsIdentifier("lumberjack"),
            Items.DIAMOND_AXE, Items.WOODEN_AXE,
            new HashSet<>()
        );

        LUMBERJACK.addReward(
            getIdentifiedBreakReward(
                "logs",
                Set.of(
                    Blocks.OAK_LOG, Blocks.BIRCH_LOG, Blocks.SPRUCE_LOG, Blocks.JUNGLE_LOG,
                    Blocks.DARK_OAK_LOG, Blocks.ACACIA_LOG,
                    Blocks.MANGROVE_LOG, Blocks.CHERRY_LOG, Blocks.PALE_OAK_LOG
                ),
                0.002, 0.05, 0.02
            )
        );
    }

    public static final JobDefinition FARMER; static {
        FARMER = new JobDefinition(
            SolaceJobs.solaceJobsIdentifier("farmer"),
            Items.DIAMOND_HOE, Items.WOODEN_HOE,
            new HashSet<>()
        );

        FARMER.addReward(
            getIdentifiedBreakReward(
                "harvest",
                Set.of(
                    Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS,
                    Blocks.PUMPKIN, Blocks.MELON,
                    Blocks.SUGAR_CANE, Blocks.CACTUS
                ),
                0.2, 0.15, 0.03
            )
        );
        FARMER.addReward(
            getIdentifiedReward(
                "disinfection",
                Set.of(
                    EntityTypes.MOOSHROOM
                ),
                ActionType.SHEAR,
                2.2, 0.8, 0.18
            )
        );

        Set<Object> farmlikeAnimals = Set.of(
            EntityTypes.COW, EntityTypes.CHICKEN, EntityTypes.PIG, EntityTypes.MOOSHROOM,
            EntityTypes.HORSE, EntityTypes.FOX, EntityTypes.ARMADILLO,
            EntityTypes.TURTLE, EntityTypes.AXOLOTL
        );
        FARMER.addReward(
            getIdentifiedReward(
                "husbandry",
                farmlikeAnimals,
                ActionType.BREED,
                0, 0.2, 0.05
            )
        );
        FARMER.addReward(
            getIdentifiedReward(
                "butchery",
                farmlikeAnimals,
                ActionType.KILL,
                0, 0.02, 0.08
            )
        );
    }

    public static final JobDefinition FISHER; static {
        FISHER = new JobDefinition(
            solaceJobsIdentifier("fisher"),
            Items.BREEZE_ROD, Items.STICK,
            new HashSet<>()
        );

        FISHER.addReward(
            getIdentifiedReward(
                "fishes",
                Set.of(
                    Items.COD, Items.SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH
                ),
                ActionType.FISH,
                0, 0.12, 0.05
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
                SolaceJobs.solaceJobsIdentifier(name),
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
                SolaceJobs.solaceJobsIdentifier(name),
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
