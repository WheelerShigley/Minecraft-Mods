package www.wheelershigley.me.trade_experience;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import www.wheelershigley.me.trade_experience.config.*;
import www.wheelershigley.me.trade_experience.helpers.ConfigurationHelper;
import www.wheelershigley.me.trade_experience.helpers.Registrations;

import java.util.HashMap;
import java.util.UUID;

import static www.wheelershigley.me.trade_experience.helpers.Registrations.*;

public class TradeExperience implements ModInitializer {
    public static final String MOD_ID = "trade_experience";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Configurations configurations = ConfigurationHelper.createTradeExperienceConfigurations();
    public static String experienceName = (String)configurations.getConfiguration("experience_name").getDefaultConfiguration();
    public static long cooldown = (long)configurations.getConfiguration("trade_timeout_time").getDefaultValue();

    public static final HashMap<UUID, Trade> activeTrades = new HashMap<>();

    @Override
    public void onInitialize() {
        registerPlayerClickListener();
        registerCheckTimeoutsEachTick();

        registerCommands();
        reload();
    }

    public static void reload() {
        configurations.reload();

        TradeExperience.experienceName = (String)configurations.getConfiguration("experience_name").getValue();
        cooldown = 20L * (long)configurations.getConfiguration("trade_timeout_time").getValue();
    }
}
