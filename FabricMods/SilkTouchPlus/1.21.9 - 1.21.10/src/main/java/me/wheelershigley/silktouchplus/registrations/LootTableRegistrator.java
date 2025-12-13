package me.wheelershigley.silktouchplus.registrations;

import me.wheelershigley.silktouchplus.data.InfestableBlockPair;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;

import static me.wheelershigley.silktouchplus.helpers.LootPoolHelpers.*;
import static me.wheelershigley.silktouchplus.registrations.GameRuleRegistrator.*;

public class LootTableRegistrator {
    private static Identifier getVanillaBlockIdentifier(Block block) {
        String[] name = block.getTranslationKey().split("\\.");
        return name.length < 3 ? null : Identifier.ofVanilla("blocks/"+name[2]);
    }

    public static void registerLootTables() {
        final Identifier
            BUDDING_AMETHYST_IDENTIFIER     = getVanillaBlockIdentifier(Blocks.BUDDING_AMETHYST),
            REINFORCED_DEEPSLATE_IDENTIFIER = getVanillaBlockIdentifier(Blocks.REINFORCED_DEEPSLATE),
            FARMLAND_IDENTIFIER             = getVanillaBlockIdentifier(Blocks.FARMLAND),
            DIRT_PATH_IDENTIFIER            = getVanillaBlockIdentifier(Blocks.DIRT_PATH),

            SPAWNER_IDENTIFIER              = getVanillaBlockIdentifier(Blocks.SPAWNER),
            TRIAL_SPAWNER_IDENTIFIER        = getVanillaBlockIdentifier(Blocks.TRIAL_SPAWNER),
            VAULT_IDENTIFIER                = getVanillaBlockIdentifier(Blocks.VAULT),

            SUSPICIOUS_GRAVEL_IDENTIFIER    = getVanillaBlockIdentifier(Blocks.SUSPICIOUS_GRAVEL),
            SUSPICIOUS_SAND_IDENTIFIER      = getVanillaBlockIdentifier(Blocks.SUSPICIOUS_SAND),

            INFESTED_STONE_IDENTIFIER                   = getVanillaBlockIdentifier(Blocks.INFESTED_STONE),
            INFESTED_COBBLESTONE_IDENTIFIER             = getVanillaBlockIdentifier(Blocks.INFESTED_COBBLESTONE),
            INFESTED_STONE_BRICKS_IDENTIFIER            = getVanillaBlockIdentifier(Blocks.INFESTED_STONE_BRICKS),
            INFESTED_MOSSY_STONE_BRICKS_IDENTIFIER      = getVanillaBlockIdentifier(Blocks.INFESTED_MOSSY_STONE_BRICKS),
            INFESTED_CRACKED_STONE_BRICKS_IDENTIFIER    = getVanillaBlockIdentifier(Blocks.INFESTED_CRACKED_STONE_BRICKS),
            INFESTED_CHISELED_STONE_BRICKS_IDENTIFIER   = getVanillaBlockIdentifier(Blocks.INFESTED_CHISELED_STONE_BRICKS),
            INFESTED_DEEPSLATE_IDENTIFIER               = getVanillaBlockIdentifier(Blocks.INFESTED_DEEPSLATE)
        ;
        ArrayList< Pair<InfestableBlockPair, Identifier> > infestedBlocks = new ArrayList<>(); {
            infestedBlocks.add(
                new Pair<>(
                    new InfestableBlockPair(Blocks.STONE, Blocks.INFESTED_STONE),
                    INFESTED_STONE_IDENTIFIER
                )
            );
            infestedBlocks.add(
                new Pair<>(
                    new InfestableBlockPair(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE),
                    INFESTED_COBBLESTONE_IDENTIFIER
                )
            );
            infestedBlocks.add(
                new Pair<>(
                        new InfestableBlockPair(Blocks.STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS),
                    INFESTED_STONE_BRICKS_IDENTIFIER
                )
            );
            infestedBlocks.add(
                new Pair<>(
                    new InfestableBlockPair(Blocks.MOSSY_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS),
                    INFESTED_MOSSY_STONE_BRICKS_IDENTIFIER
                )
            );
            infestedBlocks.add(
                new Pair<>(
                    new InfestableBlockPair(Blocks.CRACKED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS),
                    INFESTED_CRACKED_STONE_BRICKS_IDENTIFIER
                )
            );
            infestedBlocks.add(
                new Pair<>(
                    new InfestableBlockPair(Blocks.CHISELED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS),
                    INFESTED_CHISELED_STONE_BRICKS_IDENTIFIER
                )
            );
            infestedBlocks.add(
                new Pair<>(
                    new InfestableBlockPair(Blocks.DEEPSLATE, Blocks.INFESTED_DEEPSLATE),
                    INFESTED_DEEPSLATE_IDENTIFIER
                )
            );
        }

        LootTableEvents.MODIFY.register(
            (key, tableBuilder, source, registries) -> {
                Identifier identifier = key.getValue();

                assert BUDDING_AMETHYST_IDENTIFIER != null;
                if( BUDDING_AMETHYST_IDENTIFIER.equals(identifier) ) {
                    dropsWithSilkTouchPickaxe(
                        tableBuilder,
                        Blocks.BUDDING_AMETHYST,
                        null,
                        registries,
                        SILKTOUCH_BUDDING_AMETHYST
                    );
                }

                assert REINFORCED_DEEPSLATE_IDENTIFIER != null;
                if( REINFORCED_DEEPSLATE_IDENTIFIER.equals(identifier) ) {
                    dropsWithSilkTouchPickaxe(
                        tableBuilder,
                        Blocks.REINFORCED_DEEPSLATE,
                        null,
                        registries,
                        SILKTOUCH_REINFORCED_DEEPSLATE
                    );
                }

                assert SPAWNER_IDENTIFIER != null;
                if( SPAWNER_IDENTIFIER.equals(identifier) ) {
                    dropsSpawnerNBTWithSilkTouchPickaxe(
                        tableBuilder,
                        Blocks.SPAWNER,
                        registries,
                        SILKTOUCH_SPAWNER
                    );
                }

                assert SUSPICIOUS_GRAVEL_IDENTIFIER != null;
                if( SUSPICIOUS_GRAVEL_IDENTIFIER.equals(identifier) ) {
                    dropsSuspiciousWithSilkTouchShovel(
                        tableBuilder,
                        Blocks.SUSPICIOUS_GRAVEL,
                        registries,
                        SILKTOUCH_SUSPICIOUS_GRAVEL
                    );
                }

                assert SUSPICIOUS_SAND_IDENTIFIER != null;
                if( SUSPICIOUS_SAND_IDENTIFIER.equals(identifier) ) {
                    dropsSuspiciousWithSilkTouchShovel(
                        tableBuilder,
                        Blocks.SUSPICIOUS_SAND,
                        registries,
                        SILKTOUCH_SUSPICIOUS_SAND
                    );
                }

                assert TRIAL_SPAWNER_IDENTIFIER != null;
                if( TRIAL_SPAWNER_IDENTIFIER.equals(identifier) ) {
                    dropsTrialSpawnerNBTWithSilkTouchPickaxe(
                        tableBuilder,
                        Blocks.TRIAL_SPAWNER,
                        registries,
                        SILKTOUCH_TRIAL_SPAWNER
                    );
                }

                assert VAULT_IDENTIFIER != null;
                if( VAULT_IDENTIFIER.equals(identifier) ) {
                    dropVaultNBTWithSilkTouchPickaxe(
                        tableBuilder,
                        Blocks.VAULT,
                        registries,
                        SILKTOUCH_VAULT
                    );
                }
            }
        );

        LootTableEvents.REPLACE.register(
            (key, original, source, registries) -> {
                Identifier identifier = key.getValue();
                LootTable.Builder tableBuilder = new LootTable.Builder();

                assert FARMLAND_IDENTIFIER != null;
                if( FARMLAND_IDENTIFIER.equals(identifier) ) {
                    return dropsWithSilkTouch(
                        tableBuilder,
                        Blocks.FARMLAND,
                        Blocks.DIRT,
                        registries,
                        SILKTOUCH_FARMLAND
                    );
                }

                assert DIRT_PATH_IDENTIFIER != null;
                if( DIRT_PATH_IDENTIFIER.equals(identifier) ) {
                    return dropsWithSilkTouch(
                        tableBuilder,
                        Blocks.DIRT_PATH,
                        Blocks.DIRT,
                        registries,
                        SILKTOUCH_DIRT_PATH
                    );
                }

                for(Pair<InfestableBlockPair, Identifier> infestedBlock : infestedBlocks) {
                    if( infestedBlock.getRight().equals(identifier) ) {
                        return dropsWithOnlySilkTouch(
                            tableBuilder,
                            infestedBlock.getLeft().getInfestedBlock(),
                            infestedBlock.getLeft().getUninfestedBlock(),
                            registries,
                            SILKTOUCH_INFESTED_BLOCKS
                        );
                    }
                }

                return null;
            }
        );
    }
}
