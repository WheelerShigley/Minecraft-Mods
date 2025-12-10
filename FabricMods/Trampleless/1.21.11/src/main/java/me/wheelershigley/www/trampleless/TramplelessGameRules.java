package me.wheelershigley.www.trampleless;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;

public class TramplelessGameRules {
    public static GameRule<Boolean>
        FARMLAND_TRAMPLING,
        FEATHER_FALLING_TRAMPLING
    ;
    public static void registerGameRules() {
        FARMLAND_TRAMPLING = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(
                Identifier.of(Trampleless.MOD_ID, "farmland_trampling")
            )
        ;
        FEATHER_FALLING_TRAMPLING = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(
                Identifier.of(Trampleless.MOD_ID, "feather_falling_trampling")
            )
        ;
    }
}
