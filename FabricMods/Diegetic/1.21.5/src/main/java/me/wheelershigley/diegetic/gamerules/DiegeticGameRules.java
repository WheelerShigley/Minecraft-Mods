package me.wheelershigley.diegetic.gamerules;

import me.wheelershigley.diegetic.Diegetic;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

import java.util.function.BiConsumer;

public class DiegeticGameRules {
    public static GameRules.Key<GameRules.BooleanRule>
        CLOCK_DISPLAYS_TIME,
        CLOCK_USES_REAL_TIME,
        COMPASS_COORDINATES,
        LODESTONE_COMPASS_RELATIVE_COORDINATES,
        RECOVERY_COMPASS_RELATIVE_COORDINATES,
        SLIME_CHUNK_CHECKING
    ;

    private static < T extends GameRules.Rule<T> > GameRules.Key<GameRules.BooleanRule> register(String name, boolean default_value, BiConsumer<MinecraftServer, GameRules.BooleanRule> updateCall) {
        return GameRuleRegistry.register(
            name,
            GameRules.Category.PLAYER,
            GameRuleFactory.createBooleanRule(default_value, updateCall)
        );
    }

    public static void registerGameRules() {
        CLOCK_DISPLAYS_TIME = register(
            "diegeticClockDisplaysTime",
            true,
            (server, rule) -> {
                Diegetic.diegeticClockDisplaysTime = rule.get();
            }
        );
        CLOCK_USES_REAL_TIME = register(
            "diegeticClockUsesServerTime",
            false,
            (server, rule) -> {
                Diegetic.diegeticClockUsesServerTime = rule.get();
            }
        );
        COMPASS_COORDINATES = register(
            "diegeticCompassCoordinates",
            true,
            (server, rule) -> {
                Diegetic.diegeticCompassCoordinates = rule.get();
            }
        );
        LODESTONE_COMPASS_RELATIVE_COORDINATES = register(
            "diegeticLodestoneCompassRelativeCoordinates",
            true,
            (server, rule) -> {
                Diegetic.diegeticLodestoneCompassRelativeCoordinates = rule.get();
            }
        );
        RECOVERY_COMPASS_RELATIVE_COORDINATES = register(
            "diegeticRecoveryCompassRelativeCoordinates",
            true,
            (server, rule) -> {
                Diegetic.diegeticRecoveryCompassRelativeCoordinates = rule.get();
            }
        );
        SLIME_CHUNK_CHECKING = register(
            "diegeticSlimeChunkChecking",
            true,
            (server, rule) -> {
                Diegetic.diegeticSlimeChunkChecking = rule.get();
            }
        );
    }
}
