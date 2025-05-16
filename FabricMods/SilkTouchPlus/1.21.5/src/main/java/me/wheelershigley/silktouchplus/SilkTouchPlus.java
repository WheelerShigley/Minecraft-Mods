package me.wheelershigley.silktouchplus;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.wheelershigley.silktouchplus.registrations.GameRuleRegistrator.*;
import static me.wheelershigley.silktouchplus.registrations.LootTableRegistrator.*;

public class SilkTouchPlus implements ModInitializer {
    public static final String MOD_ID = "silktouchplus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean
        budding_amethyst = true,
        reinforced_deepslate = true,
        spawner = true,
        suspicious_sand = true,
        suspicious_gravel = true,
        trial_spawner = true,
        vault = true
    ;

    /* TODO
     * Add message which says that a restart is required for gamerule changes
     * Farmland
     * (vanilla-like) Shreikers
     * Dirt-path
     * Cakes
     * Dragon Egg?
     * infestedStones drops?
     *
     * Shears+ mod? (Tall grass)
     */

    @Override
    public void onInitialize() {
        registerGameRules();
        registerLootTables();
    }

    public static void reload(MinecraftServer server) {
        GameRules gameRules = server.getGameRules();
        budding_amethyst        = gameRules.getBoolean(SILKTOUCH_BUDDING_AMETHYST);
        reinforced_deepslate    = gameRules.getBoolean(SILKTOUCH_REINFORCED_DEEPSLATE);
        spawner                 = gameRules.getBoolean(SILKTOUCH_SPAWNER);
        suspicious_sand         = gameRules.getBoolean(SILKTOUCH_SUSPICIOUS_SAND);
        suspicious_gravel       = gameRules.getBoolean(SILKTOUCH_SUSPICIOUS_GRAVEL);
        trial_spawner           = gameRules.getBoolean(SILKTOUCH_TRIAL_SPAWNER);
        vault                   = gameRules.getBoolean(SILKTOUCH_VAULT);

        registerLootTables();
    }
}