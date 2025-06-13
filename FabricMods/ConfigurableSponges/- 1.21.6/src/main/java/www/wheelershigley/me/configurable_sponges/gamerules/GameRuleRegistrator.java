package www.wheelershigley.me.configurable_sponges.gamerules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class GameRuleRegistrator {
    public static GameRules.Key<GameRules.IntRule> SPONGE_DEPTH;
    public static GameRules.Key<GameRules.BooleanRule> SPONGE_WATER, SPONGE_LAVA, SPONGE_POWDERED_SNOW;

    private static < T extends GameRules.Rule<T> > GameRules.Key<GameRules.BooleanRule> register(String name, boolean default_value) {
        return GameRuleRegistry.register(
            name,
            GameRules.Category.MISC,
            GameRuleFactory.createBooleanRule(default_value)
        );
    }

    public static void registerGameRules() {
        SPONGE_DEPTH = GameRuleRegistry.register(
            "spongeRange",
            GameRules.Category.MISC,
            GameRuleFactory.createIntRule(6, 0, 1171)
        );

        SPONGE_WATER            = register("spongeWater",           true    );
        SPONGE_LAVA             = register("spongeLava",            false   );
        SPONGE_POWDERED_SNOW    = register("spongePowderedSnow",    false   );
    }
}
