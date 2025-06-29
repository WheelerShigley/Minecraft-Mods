package me.wheelershigley.silktouchplus;

import me.wheelershigley.silktouchplus.data.GameRuleLootFunction;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.wheelershigley.silktouchplus.registrations.CakeDrops.registerCakeDrop;
import static me.wheelershigley.silktouchplus.registrations.GameRuleRegistrator.*;
import static me.wheelershigley.silktouchplus.registrations.LootTableRegistrator.*;

public class SilkTouchPlus implements ModInitializer {
    public static final String MOD_ID = "silktouch_plus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        GameRuleLootFunction.register();
        registerGameRules();
        registerLootTables();

        registerCakeDrop();
    }
}