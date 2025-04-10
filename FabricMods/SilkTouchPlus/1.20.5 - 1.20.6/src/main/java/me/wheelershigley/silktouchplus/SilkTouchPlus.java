package me.wheelershigley.silktouchplus;

import external.kaupenjoe.ModConfigs;
import me.wheelershigley.silktouchplus.helpers.LootPoolHelpers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SilkTouchPlus implements ModInitializer {
    public static final String MOD_ID = "silktouchplus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModConfigs.registerConfigs();

        /*silk-touch ability loot table modifications*/ {
            HashMap<Block, Identifier> Identifiers; {
                Identifiers = new HashMap<>();

                Identifiers.put(
                    Blocks.BUDDING_AMETHYST,
                    new Identifier("minecraft", "blocks/budding_amethyst")
                );
                Identifiers.put(
                    Blocks.REINFORCED_DEEPSLATE,
                    new Identifier("minecraft", "blocks/reinforced_deepslate")
                );
                Identifiers.put(
                    Blocks.SPAWNER,
                    new Identifier("minecraft", "blocks/spawner")
                );
                Identifiers.put(
                    Blocks.SUSPICIOUS_GRAVEL,
                    new Identifier("minecraft", "blocks/suspicious_gravel")
                );
                Identifiers.put(
                    Blocks.SUSPICIOUS_SAND,
                    new Identifier("minecraft", "blocks/suspicious_sand")
                );
                /*Identifiers.put(
                    Blocks.TRIAL_SPAWNER,
                    new Identifier("minecraft", "blocks/trial_spawner")
                );*/
            }

            LootTableEvents.MODIFY.register(
                (RegistryKey<LootTable> key, LootTable.Builder tableBuilder, LootTableSource source) -> {
                    Identifier identifier = key.getValue();

                    if(ModConfigs.BUDDING_AMETHYST && Identifiers.get(Blocks.BUDDING_AMETHYST).equals(identifier) ) {
                        LootPoolHelpers.dropsWithSilkTouchPickaxe(tableBuilder, Blocks.BUDDING_AMETHYST);
                    }
                    if(ModConfigs.REINFORCED_DEEPSLATE && Identifiers.get(Blocks.REINFORCED_DEEPSLATE).equals(identifier) ) {
                        LootPoolHelpers.dropsWithSilkTouchPickaxe(tableBuilder, Blocks.REINFORCED_DEEPSLATE);
                    }
                    if(ModConfigs.SPAWNER && Identifiers.get(Blocks.SPAWNER).equals(identifier) ) {
                        LootPoolHelpers.dropsSpawnerNBTWithSilkTouchPickaxe(tableBuilder, Blocks.SPAWNER);
                    }
                    if(ModConfigs.SUSPICIOUS_GRAVEL && Identifiers.get(Blocks.SUSPICIOUS_GRAVEL).equals(identifier) ) {
                        LootPoolHelpers.dropsWithSilkTouchShovel(tableBuilder, Blocks.SUSPICIOUS_GRAVEL);
                    }
                    if(ModConfigs.SUSPICIOUS_SAND && Identifiers.get(Blocks.SUSPICIOUS_SAND).equals(identifier) ) {
                        LootPoolHelpers.dropsWithSilkTouchShovel(tableBuilder, Blocks.SUSPICIOUS_SAND);
                    }
                    /*if(ModConfigs.TRIAL_SPAWNER && Identifiers.get(Blocks.TRIAL_SPAWNER).equals(identifier) ) {
                        //TODO: TRIAL_SPAWNER NBT (pickaxe)
                        LootPoolHelpers.dropsSpawnerNBTWithSilkTouchPickaxe(tableBuilder, Blocks.TRIAL_SPAWNER);
                    }*/
                }
            );
        }

        //display configurations
        List<String> configsList; {
            configsList = new ArrayList<>();

            if(ModConfigs.BUDDING_AMETHYST)     { configsList.add("Budding-Amethyst");     }
            if(ModConfigs.REINFORCED_DEEPSLATE) { configsList.add("Reinforced-Deepslate"); }
            if(ModConfigs.SPAWNER)              { configsList.add("Spawner");              }
            if(ModConfigs.SUSPICIOUS_GRAVEL)    { configsList.add("Suspicious-Gravel");    }
            if(ModConfigs.SUSPICIOUS_SAND)      { configsList.add("Suspicious-Sand");      }
            //if(ModConfigs.TRIAL_SPAWNER)        { configsList.add("Trial-Spawner");        }
        }
        LOGGER.info(configsList.toString() + " are now Silk_Touch-able.");
    }
}
