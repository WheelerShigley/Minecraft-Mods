package www.wheelershigley.me.configurable_sponges;

import net.fabricmc.api.ModInitializer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import static www.wheelershigley.me.configurable_sponges.gamerules.GameRuleRegistrator.registerGameRules;

public class ConfigurableSponges implements ModInitializer {
    public static final String MOD_ID = "configurable_sponges";
//    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override public void onInitialize() {
        registerGameRules();
    }
}
