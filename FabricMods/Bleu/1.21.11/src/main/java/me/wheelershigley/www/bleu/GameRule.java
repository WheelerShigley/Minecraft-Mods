package me.wheelershigley.www.bleu;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.rule.GameRuleCategory;

public class GameRule {
    public static void registerGameRule() {
        Bleu.BLUE_VARIANT_RARITY = GameRuleBuilder
            .forInteger(1200)
            .minValue(1)
            .category(GameRuleCategory.MOBS)
            .buildAndRegister(
                Identifier.of(Bleu.MOD_ID,"blue_variant_rarity")
            )
        ;
    }
}
