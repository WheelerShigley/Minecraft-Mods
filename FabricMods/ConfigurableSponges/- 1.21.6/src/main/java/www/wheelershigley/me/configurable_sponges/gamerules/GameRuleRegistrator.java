package www.wheelershigley.me.configurable_sponges.gamerules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class GameRuleRegistrator {
    public static GameRules.Key<GameRules.IntRule> SPONGE_DEPTH;

    public static void registerGameRules() {
        SPONGE_DEPTH = GameRuleRegistry.register(
            "spongeRange",
            GameRules.Category.MISC,
            GameRuleFactory.createIntRule(6, 0, 1171)
        );
    }
}
