package me.wheelershigley.silktouchplus.helpers;

import me.wheelershigley.silktouchplus.data.GameRuleLootFunction;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.component.ComponentPredicateTypes;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.item.*;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.GameRules;

import java.util.List;
import java.util.concurrent.CompletableFuture;

//I used VanillaLootTableGenerator.class as a reference for the static members of this class
public class LootPoolHelpers extends FabricBlockLootTableProvider {
    protected LootPoolHelpers(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }
    @Override public void generate() {}

    //BlockLootTableGenerator.createSilkTouchCondition()
    public static LootCondition.Builder silkTouchCondition(RegistryWrapper.WrapperLookup registries) {
        return MatchToolLootCondition.builder(
            ItemPredicate.Builder.create().components(
                ComponentsPredicate.Builder.create().partial(
                    ComponentPredicateTypes.ENCHANTMENTS,
                    EnchantmentsPredicate.enchantments(
                        List.of(
                            new EnchantmentPredicate(
                                registries.getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH),
                                NumberRange.IntRange.atLeast(1)
                            )
                        )
                    )
                ).build()
            )
        );
    }

    public static LootTable dropsWithSilkTouch(
            LootTable.Builder tableBuilder,
            Block drop,
            Block defaultDrop,
            RegistryWrapper.WrapperLookup registries,
            GameRules.Key<GameRules.BooleanRule> gamerule
    ) {
        LootCondition.Builder silkTouchCondition = silkTouchCondition(registries);
        LootPool.Builder builder = LootPool.builder()
            .apply(new GameRuleLootFunction(gamerule) )
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop).conditionally(silkTouchCondition)
            )
            .with(
                ItemEntry.builder(defaultDrop).conditionally( silkTouchCondition.invert() )
            )
        ;

        tableBuilder.pool(builder.build());

        builder = LootPool.builder()
            .apply( new GameRuleLootFunction(gamerule, true) )
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(
                ItemEntry.builder(defaultDrop)
            )
        ;
        return tableBuilder.pool( builder.build() ).build();
    }
    public static LootTable dropsWithOnlySilkTouch(
            LootTable.Builder tableBuilder,
            Block drop,
            Block defaultDrop,
            RegistryWrapper.WrapperLookup registries,
            GameRules.Key<GameRules.BooleanRule> gamerule
    ) {
        LootCondition.Builder silkTouchCondition = silkTouchCondition(registries);
        LootPool.Builder builder = LootPool.builder()
            .apply(new GameRuleLootFunction(gamerule) )
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop).conditionally(silkTouchCondition)
            )
        ;

        if(defaultDrop != null) {
            tableBuilder.pool( builder.build() );

            builder = LootPool.builder()
                .apply( new GameRuleLootFunction(gamerule, true) )
                .rolls( ConstantLootNumberProvider.create(1.0F) )
                .with(
                    ItemEntry.builder(defaultDrop).conditionally(silkTouchCondition)
                )
            ;
        }
        return tableBuilder.pool( builder.build() ).build();
    }

    private static LootCondition.Builder pickaxesCondition(RegistryWrapper.WrapperLookup registries) {
        return MatchToolLootCondition.builder(
            ItemPredicate.Builder.create().tag(
                registries.getOrThrow(RegistryKeys.ITEM),
                ItemTags.PICKAXES
            )
        );
    }

    public static LootTable dropsWithSilkTouchPickaxe(
        LootTable.Builder tableBuilder,
        Block drop,
        Block defaultDrop,
        RegistryWrapper.WrapperLookup registries,
        GameRules.Key<GameRules.BooleanRule> gamerule
    ) {
        LootCondition.Builder silkTouchCondition = silkTouchCondition(registries);
        LootPool.Builder builder = LootPool.builder()
            .apply(new GameRuleLootFunction(gamerule) )
            .rolls( ConstantLootNumberProvider.create(1.0F) )
            .with(
                ItemEntry.builder(drop)
                    .conditionally( pickaxesCondition(registries) )
                    .conditionally(silkTouchCondition)
            )
        ;

        if(defaultDrop != null) {
            tableBuilder.pool( builder.build() );

            builder = LootPool.builder()
                .apply( new GameRuleLootFunction(gamerule, true) )
                .rolls( ConstantLootNumberProvider.create(1.0F) )
                .with(
                    ItemEntry.builder(defaultDrop)
                        .conditionally( pickaxesCondition(registries) )
                        .conditionally(silkTouchCondition)
                )
            ;
        }

        return tableBuilder.pool( builder.build() ).build();
    }

    public static BlockEntityType<?> getBlockEntityType(Block block) {
        return switch(block) {
//            case BannerBlock banner -> BlockEntityType.BANNER;
//            case ChestBlock chest -> BlockEntityType.CHEST;
//            case FurnaceBlock furnace -> BlockEntityType.FURNACE;
//            case SignBlock sign -> BlockEntityType.SIGN;
//            case SkullBlock skull -> BlockEntityType.SKULL;
//            case BarrelBlock barrel -> BlockEntityType.BARREL;
//            case BeaconBlock beacon -> BlockEntityType.BEACON;
//            case BellBlock bell -> BlockEntityType.BELL;
//            case BrewingStandBlock brewingStand -> BlockEntityType.BREWING_STAND;
            case BrushableBlock brushable -> BlockEntityType.BRUSHABLE_BLOCK;
//            case CampfireBlock campfire -> BlockEntityType.CAMPFIRE;
//            case ChiseledBookshelfBlock chiseledBookshelf -> BlockEntityType.CHISELED_BOOKSHELF;
//            case CommandBlock command -> BlockEntityType.COMMAND_BLOCK;
//            case ConduitBlock conduitBlock -> BlockEntityType.CONDUIT;
//            case CopperGolemStatueBlock copperGolemStatue -> BlockEntityType.COPPER_GOLEM_STATUE;
//            case CrafterBlock crafter -> BlockEntityType.CRAFTER;
//            case CreakingHeartBlock creakingHeart -> BlockEntityType.CREAKING_HEART;
//            case DaylightDetectorBlock daylightDetector -> BlockEntityType.DAYLIGHT_DETECTOR;
//            case DecoratedPotBlock decoratedPot -> BlockEntityType.DECORATED_POT;
//            case DispenserBlock dispenser -> BlockEntityType.DISPENSER;
//            case EnchantingTableBlock enchantingTable -> BlockEntityType.ENCHANTING_TABLE;
//            case EndGatewayBlock endGateway -> BlockEntityType.END_GATEWAY;
//            case EndPortalBlock endPortal -> BlockEntityType.END_PORTAL;
//            case HopperBlock hopper -> BlockEntityType.HOPPER;
//            case JukeboxBlock jukeBox -> BlockEntityType.JUKEBOX;
//            case LecternBlock lectern -> BlockEntityType.LECTERN;
//            case PistonExtensionBlock pistonExtension -> BlockEntityType.PISTON;
//            case SculkCatalystBlock sculkCatalyst -> BlockEntityType.SCULK_CATALYST;
//            case SculkSensorBlock sculkSensor -> BlockEntityType.SCULK_SENSOR;
//            case SculkShriekerBlock sculkShrieker -> BlockEntityType.SCULK_SHRIEKER;
//            case ShelfBlock shelf -> BlockEntityType.SHELF;
//            case ShulkerBoxBlock shulkerBox -> BlockEntityType.SHULKER_BOX;
            case SpawnerBlock spawner -> BlockEntityType.MOB_SPAWNER;
//            case StructureBlock structure -> BlockEntityType.STRUCTURE_BLOCK;
//            case TestBlock test -> BlockEntityType.TEST_BLOCK;
//            case TestInstanceBlock testInstance -> BlockEntityType.TEST_INSTANCE_BLOCK;
            case TrialSpawnerBlock trialSpawner -> BlockEntityType.TRIAL_SPAWNER;
            case VaultBlock vault -> BlockEntityType.VAULT;
            default -> null;
        };
    }
}
