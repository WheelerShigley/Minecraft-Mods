package external.kaupenjoe;

import com.mojang.datafixers.util.Pair;

import static me.wheelershigley.silktouchplus.SilkTouchPlus.MOD_ID;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static boolean BUDDING_AMETHYST, REINFORCED_DEEPSLATE, SPAWNER, SUSPICIOUS_SAND, SUSPICIOUS_GRAVEL, TRIAL_SPAWNER, VAULT;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(MOD_ID).provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair( new Pair<>("silktouchplus.budding_amethyst",     true) );
        configs.addKeyValuePair( new Pair<>("silktouchplus.reinforced_deepslate", true) );
        configs.addKeyValuePair( new Pair<>("silktouchplus.spawner",              true) );
        configs.addKeyValuePair( new Pair<>("silktouchplus.suspicious_gravel",    true) );
        configs.addKeyValuePair( new Pair<>("silktouchplus.suspicious_sand",      true) );
        configs.addKeyValuePair( new Pair<>("silktouchplus.trial_spawner",        true) );
        configs.addKeyValuePair( new Pair<>("silktouchplus.vault",                true) );
    }

    private static void assignConfigs() {
        BUDDING_AMETHYST =     CONFIG.getOrDefault("silktouchplus.budding_amethyst",     true);
        REINFORCED_DEEPSLATE = CONFIG.getOrDefault("silktouchplus.reinforced_deepslate", true);
        SPAWNER =              CONFIG.getOrDefault("silktouchplus.spawner",              true);
        SUSPICIOUS_GRAVEL =    CONFIG.getOrDefault("silktouchplus.suspicious_gravel",    true);
        SUSPICIOUS_SAND =      CONFIG.getOrDefault("silktouchplus.suspicious_sand",      true);
        TRIAL_SPAWNER =        CONFIG.getOrDefault("silktouchplus.trial_spawner",        true);
        VAULT =                CONFIG.getOrDefault("silktouchplus.vault",                true);
    }
}
