package me.wheelershigley.silktouchplus.registrations;

import me.wheelershigley.silktouchplus.SilkTouchPlus;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;

import java.util.HashMap;

import static me.wheelershigley.silktouchplus.helpers.LootPoolHelpers.*;
import static me.wheelershigley.silktouchplus.registrations.GameRuleRegistrator.*;

public class LootTableRegistrator {
    private static final HashMap<Block, Identifier> Identifiers; static {
        Identifiers = new HashMap<>();

        Identifiers.put(
            Blocks.BUDDING_AMETHYST,
            Identifier.of("minecraft", "blocks/budding_amethyst")
        );
        Identifiers.put(
            Blocks.REINFORCED_DEEPSLATE,
            Identifier.of("minecraft", "blocks/reinforced_deepslate")
        );
        Identifiers.put(
            Blocks.SPAWNER,
            Identifier.of("minecraft", "blocks/spawner")
        );
        Identifiers.put(
            Blocks.SUSPICIOUS_GRAVEL,
            Identifier.of("minecraft", "blocks/suspicious_gravel")
        );
        Identifiers.put(
            Blocks.SUSPICIOUS_SAND,
            Identifier.of("minecraft", "blocks/suspicious_sand")
        );
        Identifiers.put(
            Blocks.TRIAL_SPAWNER,
            Identifier.of("minecraft", "blocks/trial_spawner")
        );
        Identifiers.put(
            Blocks.VAULT,
            Identifier.of("minecraft", "blocks/vault")
        );
        Identifiers.put(
            Blocks.FARMLAND,
            Identifier.of("minecraft", "blocks/farmland")
        );
    }

    public static void registerLootTables() {
        LootTableEvents.MODIFY.register(
            (key, tableBuilder, source, registries) -> {
                Identifier identifier = key.getValue();

                if( Identifiers.get(Blocks.BUDDING_AMETHYST).equals(identifier) ) {
                    dropsWithSilkTouchPickaxe(
                        tableBuilder,
                        Blocks.BUDDING_AMETHYST,
                        registries,
                        SILKTOUCH_BUDDING_AMETHYST
                    );
                }
                if( Identifiers.get(Blocks.REINFORCED_DEEPSLATE).equals(identifier) ) {
                    dropsWithSilkTouchPickaxe(
                        tableBuilder,
                        Blocks.REINFORCED_DEEPSLATE,
                        registries,
                        SILKTOUCH_REINFORCED_DEEPSLATE
                    );
                }
                if( Identifiers.get(Blocks.SPAWNER).equals(identifier) ) {
                    dropsSpawnerNBTWithSilkTouchPickaxe(
                        tableBuilder,
                        Blocks.SPAWNER,
                        registries,
                        SILKTOUCH_SPAWNER
                    );
                }
                if( Identifiers.get(Blocks.SUSPICIOUS_GRAVEL).equals(identifier) ) {
                    dropsSuspiciousWithSilkTouchShovel(
                        tableBuilder,
                        Blocks.SUSPICIOUS_GRAVEL,
                        registries,
                        SILKTOUCH_SUSPICIOUS_GRAVEL
                    );
                }
                if( Identifiers.get(Blocks.SUSPICIOUS_SAND).equals(identifier) ) {
                    dropsSuspiciousWithSilkTouchShovel(
                        tableBuilder,
                        Blocks.SUSPICIOUS_SAND,
                        registries,
                        SILKTOUCH_SUSPICIOUS_SAND
                    );
                }
                if( Identifiers.get(Blocks.TRIAL_SPAWNER).equals(identifier) ) {
                    dropsTrialSpawnerNBTWithSilkTouchPickaxe(
                        tableBuilder,
                        Blocks.TRIAL_SPAWNER,
                        registries,
                        SILKTOUCH_TRIAL_SPAWNER
                    );
                }
                if( Identifiers.get(Blocks.VAULT).equals(identifier) ) {
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

                if( Identifiers.get(Blocks.FARMLAND).equals(identifier) ) {
                    dropsWithSilkTouchShovel(
                        tableBuilder,
                        Blocks.FARMLAND,
                        Blocks.DIRT,
                        registries,
                        SILKTOUCH_FARMLAND
                    );
                }
                return tableBuilder.build();
            }
        );
    }
}
