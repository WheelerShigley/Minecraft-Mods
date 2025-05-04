package www.wheelershigley.me.trade_experience;

import net.fabricmc.api.ModInitializer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

import static www.wheelershigley.me.trade_experience.helpers.Registrations.*;

/* TODO
 * - configuration for COOLDOWN
 * - configuration for experience-name
 * - Shift-click does not initiate trade
 * - icon
 */

public class TradeExperience implements ModInitializer {
//    public static final String MOD_ID = "trade_experience";
//    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final HashMap<UUID, Trade> activeTrades = new HashMap<>();

    @Override
    public void onInitialize() {
        registerCommands();
        registerPlayerClickListener();
        registerCheckTimeoutsEachTick();
    }
}
