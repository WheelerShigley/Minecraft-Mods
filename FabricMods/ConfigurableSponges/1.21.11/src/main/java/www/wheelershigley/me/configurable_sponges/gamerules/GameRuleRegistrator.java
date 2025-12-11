package www.wheelershigley.me.configurable_sponges.gamerules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;
import www.wheelershigley.me.configurable_sponges.ConfigurableSponges;

public class GameRuleRegistrator {
    public static GameRule<Integer>
        SPONGE_DEPTH
    ;
    public static GameRule<Boolean>
        SPONGE_WATER,
        SPONGE_LAVA,
        SPONGE_POWDERED_SNOW,
        WET_SPONGE_WATER,
        WET_SPONGE_LAVA,
        WET_SPONGE_POWDERED_SNOW
    ;

    private static GameRule<Integer> register(String name, int default_value, int minimum, int maximum) {
        return GameRuleBuilder
            .forInteger(default_value)
            .range(minimum, maximum)
            .category(GameRuleCategory.MISC)
            .buildAndRegister(
                Identifier.of(
                    ConfigurableSponges.MOD_ID.toLowerCase(),
                    name.toLowerCase()
                )
            )
        ;
    }
//    private static GameRule<Integer> register(String name, int default_value) {
//        return GameRuleBuilder
//            .forInteger(default_value)
//            .category(GameRuleCategory.MISC)
//            .buildAndRegister(
//                Identifier.of(ConfigurableSponges.MOD_ID, name)
//            )
//        ;
//    }
    private static GameRule<Boolean> register(String name, boolean default_value) {
        return GameRuleBuilder
        .forBoolean(default_value)
        .category(GameRuleCategory.MISC)
            .buildAndRegister(
                Identifier.of(
                    ConfigurableSponges.MOD_ID.toLowerCase(),
                    name.toLowerCase()
                )
            )
        ;
    }

    public static void registerGameRules() {
        //1171 is the maximum depth before the centered, octal number reaches an integer overflow
        SPONGE_DEPTH = register("sponges_range", 6, 0, 1171);

        SPONGE_WATER            = register("sponge_absorbs_water",          true);
        SPONGE_LAVA             = register("sponge_absorbs_lava",           false);
        SPONGE_POWDERED_SNOW    = register("sponge_absorbs_powdered_snow",  false);

        WET_SPONGE_WATER            = register("wet_sponge_absorbs_water",          false);
        WET_SPONGE_LAVA             = register("wet_sponge_absorbs_lava",           false);
        WET_SPONGE_POWDERED_SNOW    = register("wet_sponge_absorbs_powdered_snow",  false);
    }
}
