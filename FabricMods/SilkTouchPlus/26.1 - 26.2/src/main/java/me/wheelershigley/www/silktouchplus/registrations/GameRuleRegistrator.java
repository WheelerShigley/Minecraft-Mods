package me.wheelershigley.www.silktouchplus.registrations;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

import static me.wheelershigley.www.silktouchplus.SilkTouchPlus.MOD_ID;

public class GameRuleRegistrator {
    public static GameRule<Boolean>
        SILKTOUCH_BUDDING_AMETHYST,
        SILKTOUCH_REINFORCED_DEEPSLATE,
        SILKTOUCH_SPAWNER,
        SILKTOUCH_SUSPICIOUS_SAND,
        SILKTOUCH_SUSPICIOUS_GRAVEL,
        SILKTOUCH_TRIAL_SPAWNER,
        SILKTOUCH_VAULT,
        SILKTOUCH_INFESTED_BLOCKS,
        SILKTOUCH_FARMLAND,
        SILKTOUCH_DIRT_PATH,
        SILKTOUCH_CAKE
    ;

    private static < T extends GameRule<T> > GameRule<Boolean> register(String name, boolean default_value) {
        return GameRuleBuilder
            .forBoolean(default_value)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(
                Identifier.fromNamespaceAndPath(MOD_ID, name)
            )
        ;
    }
    public static void registerGameRules() {
        SILKTOUCH_BUDDING_AMETHYST      = register("silktouch_budding_amethyst",     true );
        SILKTOUCH_REINFORCED_DEEPSLATE  = register("silktouch_reinforced_deepslate", true );
        SILKTOUCH_SPAWNER               = register("silktouch_mob_spawner",          true );
        SILKTOUCH_SUSPICIOUS_SAND       = register("silktouch_suspicious_sand",      true );
        SILKTOUCH_SUSPICIOUS_GRAVEL     = register("silktouch_suspicious_gravel",    true );
        SILKTOUCH_TRIAL_SPAWNER         = register("silktouch_trial_spawner",        true );
        SILKTOUCH_VAULT                 = register("silktouch_vault",                true );
        SILKTOUCH_INFESTED_BLOCKS       = register("silktouch_infested_blocks",      true );
        SILKTOUCH_FARMLAND              = register("silktouch_farmland",             false);
        SILKTOUCH_DIRT_PATH             = register("silktouch_dirt_path",            false);
        SILKTOUCH_CAKE                  = register("silktouch_cake",                 true );
    }
}
