package me.wheelershigley.default_arms.gamerule;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class registerGameRule {
    public static GameRules.Key<GameRules.BooleanRule> ARMLESS_ARMOR_STAND_DROPS_WITH_LORE;

    public static void registerGameRule() {
        ARMLESS_ARMOR_STAND_DROPS_WITH_LORE = GameRuleRegistry.register(
            "armlessArmorStandDropsWithLore",
            GameRules.Category.PLAYER,
            GameRuleFactory.createBooleanRule(true)
        );
    }
}
