package me.wheelershigley.www.diegetic.gamerules;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import me.wheelershigley.www.diegetic.Diegetic;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.world.rule.*;

import java.util.function.ToIntFunction;

public class DiegeticGameRules {
    public static GameRule<Boolean>
        CLOCK_DISPLAYS_TIME,
        CLOCK_USES_REAL_TIME,
        COMPASS_COORDINATES,
        LODESTONE_COMPASS_RELATIVE_COORDINATES,
        RECOVERY_COMPASS_RELATIVE_COORDINATES,
        SLIME_CHUNK_CHECKING
    ;
    private static final Identifier
        CLOCK_DISPLAYS_TIME_IDENTIFIER = getCustomIdentifier("clock_displays_time"),
        CLOCK_USES_REAL_TIME_IDENTIFIER = getCustomIdentifier("clock_uses_server_time"),
        COMPASS_COORDINATES_IDENTIFIER = getCustomIdentifier("compass_coordinates"),
        LODESTONE_COMPASS_RELATIVE_COORDINATES_IDENTIFIER = getCustomIdentifier("lodestone_compass_relative_coordinates"),
        RECOVERY_COMPASS_RELATIVE_COORDINATES_IDENTIFIER = getCustomIdentifier("recovery_compass_relative_coordinates"),
        SLIME_CHUNK_CHECKING_IDENTIFIER = getCustomIdentifier("slime_chunk_checking")
    ;

    private static Identifier getCustomIdentifier(String name) {
        return Identifier.of(
            Diegetic.MOD_ID.toLowerCase(),
            name.toLowerCase()
        );
    }

    public static void registerGameRules() {
        CLOCK_DISPLAYS_TIME = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(CLOCK_DISPLAYS_TIME_IDENTIFIER)
        ;
        CLOCK_USES_REAL_TIME = GameRuleBuilder
            .forBoolean(false)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(CLOCK_USES_REAL_TIME_IDENTIFIER)
        ;
        COMPASS_COORDINATES = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(COMPASS_COORDINATES_IDENTIFIER)
        ;
        LODESTONE_COMPASS_RELATIVE_COORDINATES = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(LODESTONE_COMPASS_RELATIVE_COORDINATES_IDENTIFIER)
        ;
        RECOVERY_COMPASS_RELATIVE_COORDINATES = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(RECOVERY_COMPASS_RELATIVE_COORDINATES_IDENTIFIER)
        ;
        SLIME_CHUNK_CHECKING = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(SLIME_CHUNK_CHECKING_IDENTIFIER)
        ;
    }

    // GameRules::registerBoolRule
    private static GameRule<Boolean> registerBooleanRule(String name, GameRuleCategory category, boolean defaultValue) {
        return register(
            name,
            category,
            GameRuleType.BOOL,
            BoolArgumentType.bool(),
            Codec.BOOL,
            defaultValue,
            FeatureSet.empty(),
            GameRuleVisitor::visitBoolean,
            value -> (value ? 1 : 0)
        );
    }

    // GameRules::register
    private static <T> GameRule<T> register(
        String name,
        GameRuleCategory category,
        GameRuleType type,
        ArgumentType<T> argumentType,
        Codec<T> codec,
        T defaultValue,
        FeatureSet requiredFeatures,
        GameRules.Acceptor<T> acceptor,
        ToIntFunction<T> commandResultSupplier
    ) {
        return Registry.register(
            Registries.GAME_RULE,
            name,
            new GameRule<>(
                category,
                type,
                argumentType,
                acceptor,
                codec,
                commandResultSupplier,
                defaultValue,
                requiredFeatures
            )
        );
    }
}
