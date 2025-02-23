package external.kaupenjoe;

import com.mojang.datafixers.util.Pair;

import static me.wheelershigley.silktouchplus.SilkTouchPlus.MOD_ID;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static boolean BUDDING_AMETHYST, REINFORCED_DEEPSLATE, SPAWNER, SUSPICIOUS_SAND, SUSPICIOUS_GRAVEL, TRIAL_SPAWNER;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(MOD_ID).provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair( new Pair<>("silktouch.budding_amethyst",     true) );
        configs.addKeyValuePair( new Pair<>("silktouch.reinforced_deepslate", true) );
        configs.addKeyValuePair( new Pair<>("silktouch.spawner",              true) );
        configs.addKeyValuePair( new Pair<>("silktouch.suspicious_gravel",    true) );
        configs.addKeyValuePair( new Pair<>("silktouch.suspicious_sand",      true) );
        //configs.addKeyValuePair( new Pair<>("silktouch.trial_spawner",      true) );
    }

    private static void assignConfigs() {
        BUDDING_AMETHYST =     CONFIG.getOrDefault("silktouch.budding_amethyst",     true);
        REINFORCED_DEEPSLATE = CONFIG.getOrDefault("silktouch.reinforced_deepslate", true);
        SPAWNER =              CONFIG.getOrDefault("silktouch.spawner",              true);
        SUSPICIOUS_GRAVEL =    CONFIG.getOrDefault("silktouch.suspicious_gravel",    true);
        SUSPICIOUS_SAND =      CONFIG.getOrDefault("silktouch.suspicious_sand",      true);
        //TRIAL_SPAWNER =        CONFIG.getOrDefault("silktouch.trial_spawner",        true);
    }
}
