package me.wheelershigley.trampleless;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class TramplelessGameRules {
    public static GameRules.Key<GameRules.BooleanRule>
        FARMLAND_TRAMPLING,
        FEATHER_FALLING_TRAMPLING
    ;
    public static void registerGameRules() {
        FARMLAND_TRAMPLING = GameRuleRegistry.register(
            "farmlandTrampling",
            GameRules.Category.PLAYER,
            GameRuleFactory.createBooleanRule(
                true,
                (server, rule) -> {
                    Trampleless.farmlandTrampling = rule.get();
                }
            )
        );
        FEATHER_FALLING_TRAMPLING = GameRuleRegistry.register(
            "featherFallingTrampling",
            GameRules.Category.PLAYER,
            GameRuleFactory.createBooleanRule(
                false,
                (server, rule) -> {
                    Trampleless.featherFallingTrampling = rule.get();
                }
            )
        );
    }
}
