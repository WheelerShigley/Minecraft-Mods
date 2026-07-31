package me.wheelershigley.www.trampleless;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

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
                Identifier.fromNamespaceAndPath(Trampleless.MOD_ID, "farmland_trampling")
            )
        ;
        FEATHER_FALLING_TRAMPLING = GameRuleBuilder
            .forBoolean(false)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(
                Identifier.fromNamespaceAndPath(Trampleless.MOD_ID, "feather_falling_trampling")
            )
        ;
    }
}
