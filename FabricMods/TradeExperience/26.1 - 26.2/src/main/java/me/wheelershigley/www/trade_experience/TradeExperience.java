package me.wheelershigley.www.trade_experience;

import me.wheelershigley.www.trade_experience.config.Configurations;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.wheelershigley.www.trade_experience.config.*;
import me.wheelershigley.www.trade_experience.gamerule.GameRules;
import me.wheelershigley.www.trade_experience.helpers.ConfigurationHelper;

import java.util.HashMap;
import java.util.UUID;

import static me.wheelershigley.www.trade_experience.helpers.Registrations.*;

public class TradeExperience implements ModInitializer {
    public static final String MOD_ID = "trade_experience";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Configurations configurations = ConfigurationHelper.createTradeExperienceConfigurations();
    public static String experienceName = (String)configurations.getConfiguration("experience_name").getDefaultConfiguration();

    public static final HashMap<UUID, Trade> activeTrades = new HashMap<>();

    @Override
    public void onInitialize() {
        registerPlayerClickListener();
        registerCheckTimeoutsEachTick();

        GameRules.registerGameRule();
        registerCommands();
        reload();
    }

    public static void reload() {
        configurations.reload();

        TradeExperience.experienceName = (String)configurations.getConfiguration("experience_name").getValue();
    }
}
