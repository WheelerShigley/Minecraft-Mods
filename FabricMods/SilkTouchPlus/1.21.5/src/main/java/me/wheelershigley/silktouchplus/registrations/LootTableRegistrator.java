package me.wheelershigley.silktouchplus.registrations;

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
        ArrayList< Pair<Block, Identifier> > infestedBlocks = new ArrayList<>(); {
            infestedBlocks.add( new Pair<>(Blocks.INFESTED_STONE, INFESTED_STONE_IDENTIFIER) );
            infestedBlocks.add( new Pair<>(Blocks.INFESTED_COBBLESTONE, INFESTED_COBBLESTONE_IDENTIFIER) );
            infestedBlocks.add( new Pair<>(Blocks.INFESTED_STONE_BRICKS, INFESTED_STONE_BRICKS_IDENTIFIER) );
            infestedBlocks.add( new Pair<>(Blocks.INFESTED_MOSSY_STONE_BRICKS, INFESTED_MOSSY_STONE_BRICKS_IDENTIFIER) );
            infestedBlocks.add( new Pair<>(Blocks.INFESTED_CRACKED_STONE_BRICKS, INFESTED_CRACKED_STONE_BRICKS_IDENTIFIER) );
            infestedBlocks.add( new Pair<>(Blocks.INFESTED_CHISELED_STONE_BRICKS, INFESTED_CHISELED_STONE_BRICKS_IDENTIFIER) );
            infestedBlocks.add( new Pair<>(Blocks.INFESTED_DEEPSLATE, INFESTED_DEEPSLATE_IDENTIFIER) );
        }

        LootTableEvents.MODIFY.register(
            (key, tableBuilder, source, registries) -> {
                Identifier identifier = key.getValue();

                if( BUDDING_AMETHYST_IDENTIFIER.equals(identifier) ) {
                    dropsWithSilkTouchPickaxe(
                        tableBuilder,
                        Blocks.BUDDING_AMETHYST,
                        registries,
                        SILKTOUCH_BUDDING_AMETHYST
                    );
                }
                if( REINFORCED_DEEPSLATE_IDENTIFIER.equals(identifier) ) {
                    dropsWithSilkTouchPickaxe(
                        tableBuilder,
                        Blocks.REINFORCED_DEEPSLATE,
                        registries,
                        SILKTOUCH_REINFORCED_DEEPSLATE
                    );
                }
                if( SPAWNER_IDENTIFIER.equals(identifier) ) {
                    dropsSpawnerNBTWithSilkTouchPickaxe(
                        tableBuilder,
                        Blocks.SPAWNER,
                        registries,
                        SILKTOUCH_SPAWNER
                    );
                }
                if( SUSPICIOUS_GRAVEL_IDENTIFIER.equals(identifier) ) {
                    dropsSuspiciousWithSilkTouchShovel(
                        tableBuilder,
                        Blocks.SUSPICIOUS_GRAVEL,
                        registries,
                        SILKTOUCH_SUSPICIOUS_GRAVEL
                    );
                }
                if( SUSPICIOUS_SAND_IDENTIFIER.equals(identifier) ) {
                    dropsSuspiciousWithSilkTouchShovel(
                        tableBuilder,
                        Blocks.SUSPICIOUS_SAND,
                        registries,
                        SILKTOUCH_SUSPICIOUS_SAND
                    );
                }
                if( TRIAL_SPAWNER_IDENTIFIER.equals(identifier) ) {
                    dropsTrialSpawnerNBTWithSilkTouchPickaxe(
                        tableBuilder,
                        Blocks.TRIAL_SPAWNER,
                        registries,
                        SILKTOUCH_TRIAL_SPAWNER
                    );
                }
                if( VAULT_IDENTIFIER.equals(identifier) ) {
                    dropVaultNBTWithSilkTouchPickaxe(
                        tableBuilder,
                        Blocks.VAULT,
                        registries,
                        SILKTOUCH_VAULT
                    );
                }

                for(Pair<Block, Identifier> infestedBlock : infestedBlocks) {
                    if( infestedBlock.getRight().equals(identifier) ) {
                        dropsWithSilkTouchPickaxe(
                            tableBuilder,
                            infestedBlock.getLeft(),
                            registries,
                            SILKTOUCH_INFESTED_BLOCKS
                        );
                    }
                }
            }
        );

        LootTableEvents.REPLACE.register(
            (key, original, source, registries) -> {
                Identifier identifier = key.getValue();
                LootTable.Builder tableBuilder = new LootTable.Builder();

                if( FARMLAND_IDENTIFIER.equals(identifier) ) {
                    dropsWithSilkTouch(
                        tableBuilder,
                        Blocks.FARMLAND,
                        Blocks.DIRT,
                        registries,
                        SILKTOUCH_FARMLAND
                    );
                }
                if( DIRT_PATH_IDENTIFIER.equals(identifier) ) {
                    dropsWithSilkTouch(
                        tableBuilder,
                        Blocks.DIRT_PATH,
                        Blocks.DIRT,
                        registries,
                        SILKTOUCH_DIRT_PATH
                    );
                }

                return tableBuilder.build();
            }
        );
    }
}
