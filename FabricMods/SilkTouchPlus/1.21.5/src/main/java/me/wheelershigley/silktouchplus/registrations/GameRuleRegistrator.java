package me.wheelershigley.silktouchplus.registrations;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class GameRuleRegistrator {
    public static GameRules.Key<GameRules.BooleanRule>
        SILKTOUCH_BUDDING_AMETHYST,
        SILKTOUCH_REINFORCED_DEEPSLATE,
        SILKTOUCH_SPAWNER,
        SILKTOUCH_SUSPICIOUS_SAND,
        SILKTOUCH_SUSPICIOUS_GRAVEL,
        SILKTOUCH_TRIAL_SPAWNER,
        SILKTOUCH_VAULT
//        SILKTOUCH_FARMLAND
    ;

    private static < T extends GameRules.Rule<T> > GameRules.Key<GameRules.BooleanRule> register(String name, boolean default_value) {
        return GameRuleRegistry.register(
            name,
            GameRules.Category.PLAYER,
            GameRuleFactory.createBooleanRule(default_value)
        );
    }
    public static void registerGameRules() {
        SILKTOUCH_BUDDING_AMETHYST      = register("silktouchBuddingAmethyst",      true);
        SILKTOUCH_REINFORCED_DEEPSLATE  = register("silktouchReinforcedDeepslate",  true);
        SILKTOUCH_SPAWNER               = register("silktouchSpawner",              true);
        SILKTOUCH_SUSPICIOUS_SAND       = register("silktouchSuspiciousSand",       true);
        SILKTOUCH_SUSPICIOUS_GRAVEL     = register("silktouchSuspiciousGravel",     true);
        SILKTOUCH_TRIAL_SPAWNER         = register("silktouchTrialSpawner",         true);
        SILKTOUCH_VAULT                 = register("silktouchVault",                true);
//        SILKTOUCH_FARMLAND              = register("silktouchFarmland",             false);
    }
}
