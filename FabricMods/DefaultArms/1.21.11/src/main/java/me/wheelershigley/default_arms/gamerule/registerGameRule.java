package me.wheelershigley.default_arms.gamerule;

import me.wheelershigley.default_arms.DefaultArms;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;

public class registerGameRule {
    public static GameRule<Boolean> ARMLESS_ARMOR_STAND_DROPS_WITH_LORE;

    public static void registerGameRule() {
        ARMLESS_ARMOR_STAND_DROPS_WITH_LORE = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(
                Identifier.of(
                    DefaultArms.MOD_ID.toLowerCase(),
                    "armless_armor_stands_drop_with_lore"
                )
            )
        ;
    }
}
