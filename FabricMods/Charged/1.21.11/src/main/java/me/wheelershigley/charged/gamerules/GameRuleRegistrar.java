package me.wheelershigley.charged.gamerules;

import me.wheelershigley.charged.Charged;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;

public class GameRuleRegistrar {
    public static GameRule<Boolean> ENABLE_PLAYER_HEAD_DROP;
    public static GameRule<Boolean> ENABLE_PLAYER_HEAD_DROP_TEXTURES;
    public static GameRule<Boolean> ENABLE_PLAYER_HEAD_TEXTURE_WASHING;
    public static GameRule<Integer> MAXIMUM_HEAD_DROP_COUNT;
    public static void registerGameRules() {
        ENABLE_PLAYER_HEAD_DROP = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(
                Identifier.of(Charged.MOD_ID, "player_head_drop")
            )
        ;
        ENABLE_PLAYER_HEAD_DROP_TEXTURES = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(
                Identifier.of(Charged.MOD_ID, "player_head_drop_textures")
            )
        ;
        ENABLE_PLAYER_HEAD_TEXTURE_WASHING = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(
                Identifier.of(Charged.MOD_ID, "player_head_texture_washing")
            )
        ;
        MAXIMUM_HEAD_DROP_COUNT = GameRuleBuilder
            .forInteger(-1)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(
                Identifier.of(Charged.MOD_ID, "maximum_head_drop_count")
            )
        ;

        GameRuleEvents.changeCallback(
            ENABLE_PLAYER_HEAD_TEXTURE_WASHING
        );
    }

    public static void registerGameRuleNetworking() {
        PayloadTypeRegistry.playS2C().register(
            WashingGameRulePayload.identifier,
            WashingGameRulePayload.CODEC
        );
    }
}
