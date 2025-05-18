package me.wheelershigley.silktouchplus;

import me.wheelershigley.silktouchplus.registrations.GameRuleLootFunction;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.wheelershigley.silktouchplus.registrations.CakeDrops.registerCakeDrop;
import static me.wheelershigley.silktouchplus.registrations.GameRuleRegistrator.*;
import static me.wheelershigley.silktouchplus.registrations.LootTableRegistrator.*;

public class SilkTouchPlus implements ModInitializer {
    public static final String MOD_ID = "silktouchplus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /* TODO
     * (vanilla-like) Shreikers
     * Dragon Egg?
     * infestedStones drops?
     *
     * Shears+ mod? (Tall grass)
     */

    @Override
    public void onInitialize() {
        GameRuleLootFunction.register();
        registerGameRules();
        registerLootTables();

        registerCakeDrop();
    }
}