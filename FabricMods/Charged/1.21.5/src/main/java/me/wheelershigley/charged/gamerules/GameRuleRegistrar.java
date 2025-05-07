package me.wheelershigley.charged.gamerules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class GameRuleRegistrar {
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_PLAYER_HEAD_DROP;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_PLAYER_HEAD_DROP_TEXTURES;
    public static GameRules.Key<GameRules.BooleanRule> ENABLE_PLAYER_HEAD_TEXTURE_WASHING;
    public static GameRules.Key<GameRules.IntRule    > MAXIMUM_HEAD_DROP_COUNT;
    public static void registerGameRules() {
        ENABLE_PLAYER_HEAD_DROP = GameRuleRegistry.register(
            "playerHeadDrop",
            GameRules.Category.PLAYER,
            GameRuleFactory.createBooleanRule(true)
        );
        ENABLE_PLAYER_HEAD_DROP_TEXTURES = GameRuleRegistry.register(
            "playerHeadDropTextures",
            GameRules.Category.PLAYER,
            GameRuleFactory.createBooleanRule(true)
        );
        ENABLE_PLAYER_HEAD_TEXTURE_WASHING = GameRuleRegistry.register(
            "playerHeadTextureWashing",
            GameRules.Category.PLAYER,
            GameRuleFactory.createBooleanRule(true, GameRuleSync.commonSideWashingSync)
        );
        MAXIMUM_HEAD_DROP_COUNT = GameRuleRegistry.register(
            "maxHeadDropCount",
            GameRules.Category.MOBS,
            GameRuleFactory.createIntRule(-1)
        );
    }
}
