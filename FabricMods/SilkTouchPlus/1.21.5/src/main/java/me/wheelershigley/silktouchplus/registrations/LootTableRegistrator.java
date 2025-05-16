package me.wheelershigley.silktouchplus.registrations;

import me.wheelershigley.silktouchplus.SilkTouchPlus;
import me.wheelershigley.silktouchplus.helpers.LootPoolHelpers;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

import java.util.HashMap;

import static me.wheelershigley.silktouchplus.SilkTouchPlus.*;

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
    }

    public static void registerLootTables() {
        LootTableEvents.MODIFY.register(
            (key, tableBuilder, source, registries) -> {
                Identifier identifier = key.getValue();

                //TODO elses & abstraction
                if(budding_amethyst && Identifiers.get(Blocks.BUDDING_AMETHYST).equals(identifier) ) {
                    LootPoolHelpers.dropsWithSilkTouchPickaxe(tableBuilder, Blocks.BUDDING_AMETHYST, registries);
                }
                if(reinforced_deepslate && Identifiers.get(Blocks.REINFORCED_DEEPSLATE).equals(identifier) ) {
                    LootPoolHelpers.dropsWithSilkTouchPickaxe(tableBuilder, Blocks.REINFORCED_DEEPSLATE, registries);
                }
                if(spawner && Identifiers.get(Blocks.SPAWNER).equals(identifier) ) {
                    LootPoolHelpers.dropsSpawnerNBTWithSilkTouchPickaxe(tableBuilder, Blocks.SPAWNER, registries);
                }
                if(suspicious_gravel && Identifiers.get(Blocks.SUSPICIOUS_GRAVEL).equals(identifier) ) {
                    LootPoolHelpers.dropsWithSilkTouchShovel(tableBuilder, Blocks.SUSPICIOUS_GRAVEL, registries);
                }
                if(suspicious_sand && Identifiers.get(Blocks.SUSPICIOUS_SAND).equals(identifier) ) {
                    LootPoolHelpers.dropsWithSilkTouchShovel(tableBuilder, Blocks.SUSPICIOUS_SAND, registries);
                }
                if(trial_spawner && Identifiers.get(Blocks.TRIAL_SPAWNER).equals(identifier) ) {
                    LootPoolHelpers.dropsTrialSpawnerNBTWithSilkTouchPickaxe(tableBuilder, Blocks.TRIAL_SPAWNER, registries);
                }
                if(vault && Identifiers.get(Blocks.VAULT).equals(identifier) ) {
                    LootPoolHelpers.dropVaultNBTWithSilkTouchPickaxe(tableBuilder, Blocks.VAULT, registries);
                }
            }
        );
    }
}
