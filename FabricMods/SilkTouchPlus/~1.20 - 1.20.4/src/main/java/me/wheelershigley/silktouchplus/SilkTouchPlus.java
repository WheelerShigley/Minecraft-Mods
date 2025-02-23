package me.wheelershigley.silktouchplus;

import external.LordDeathHunter.*;
import external.kaupenjoe.ModConfigs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class SilkTouchPlus implements ModInitializer {
    public static final String MOD_ID = "silktouchplus";
    public static final Logger LOGGER = getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModConfigs.registerConfigs();

        LOGGER.info("a");
        //silk-touch ability loot table modifications
        LootTableEvents.MODIFY.register(
            (RegistryKey<LootTable> key, LootTable.Builder tableBuilder, LootTableSource source) -> {
                Identifier id = key.getValue();
                if(  ModConfigs.BUDDING_AMETHYST  &&  ( new Identifier("minecraft","blocks/spawner") ).equals(id)  ) {
                    registerDrops.registerSilkTouchDropWithNBT(tableBuilder, Items.SPAWNER);
                }
                if(  ModConfigs.REINFORCED_DEEPSLATE  &&  ( new Identifier("minecraft", "blocks/reinforced_deepslate") ).equals(id)  ) {
                    registerDrops.registerSilkTouchDrop(tableBuilder, Items.REINFORCED_DEEPSLATE);
                }
                if(  ModConfigs.SPAWNER  &&  ( new Identifier("minecraft", "blocks/budding_amethyst") ).equals(id)  ) {
                    registerDrops.registerSilkTouchDrop(tableBuilder, Items.BUDDING_AMETHYST);
                    //LOGGER.info("budding_amethyst!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
                if(  ModConfigs.SUSPICIOUS_GRAVEL  &&  ( new Identifier("minecraft", "blocks/suspicious_gravel") ).equals(id)  ) {
                    registerDrops.registerSilkTouchDrop(tableBuilder, Items.SUSPICIOUS_GRAVEL);
                }
                if(  ModConfigs.SUSPICIOUS_SAND  &&  ( new Identifier("minecraft", "blocks/suspicious_sand") ).equals(id)  ) {
                    registerDrops.registerSilkTouchDrop(tableBuilder, Items.SUSPICIOUS_SAND);
                }
                /*if(  ModConfigs.TRIAL_SPAWNER  &&  ( new Identifier("minecraft", "blocks/trial_spawner") ).equals(id)  ) {
                    registerDrops.registerSilkTouchDropWithNBT(tableBuilder, Items.TRIAL_SPAWNER);
                }*/
            }
        );

        //display configurations
        List<String> configsList = new ArrayList<>(); {
            if(ModConfigs.BUDDING_AMETHYST)     { configsList.add("Budding-Amethyst");     }
            if(ModConfigs.REINFORCED_DEEPSLATE) { configsList.add("Reinforced-Deepslate"); }
            if(ModConfigs.SPAWNER)              { configsList.add("Spawner");              }
            if(ModConfigs.SUSPICIOUS_GRAVEL)    { configsList.add("Suspicious-Gravel");    }
            if(ModConfigs.SUSPICIOUS_SAND)      { configsList.add("Suspicious-Sand");      }
            //if(ModConfigs.TRIAL_SPAWNER)        { configsList.add("Trial-Spawner");        }
        }
        LOGGER.info(configsList.toString() + " are now Silk-Touch-able.");
    }
}