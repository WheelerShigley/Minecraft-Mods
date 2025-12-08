package me.wheelershigley.www.bleu;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

import java.util.function.BiConsumer;

public class RarityGameRule {
    public static GameRules.Key<GameRules.IntRule> BLUE_VARIANT_RARITY;

    private static BiConsumer<MinecraftServer, GameRules.IntRule> updateRule = (server, rule) -> {
        Bleu.BLUE_VARIANT_RARITY = rule.get();
    };

    public static void registerGameRule() {
        BLUE_VARIANT_RARITY = GameRuleRegistry.register(
            "blueAxolotlVariantRarity",
            GameRules.Category.MOBS,
            GameRuleFactory.createIntRule(1200, 1, updateRule)
        );
    }
}
