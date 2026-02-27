package www.wheelershigley.me.trade_experience.gamerule;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;
import www.wheelershigley.me.trade_experience.TradeExperience;

public class GameRules {
    public static GameRule<Boolean> INTERACT_TRADE_INITIATION;
    //public static GameRule<String> EXPERIENCE_NAME;
    public static GameRule<Integer> TRADE_TIMEOUT_TIME;

    public static void registerGameRule() {
        INTERACT_TRADE_INITIATION = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(
                Identifier.of(TradeExperience.MOD_ID, "interact_trade_initiation")
            )
        ;

        /*
        EXPERIENCE_NAME = GameRuleBuilder
            .forString("experience")
            .category(GameRuleCategory.PLAYER)
                .buildAndRegister(
                    Identifier.of(TradeExperience.MOD_ID, "experience_name")
                )
        ;
         */

        TRADE_TIMEOUT_TIME = GameRuleBuilder
            .forInteger(30*20 /*30 seconds*/)
            .minValue(0)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(
                Identifier.of(TradeExperience.MOD_ID, "trade_timeout_time")
            )
        ;
    }
}
