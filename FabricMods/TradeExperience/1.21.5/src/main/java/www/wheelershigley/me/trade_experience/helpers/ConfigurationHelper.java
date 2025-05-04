package www.wheelershigley.me.trade_experience.helpers;

import net.fabricmc.loader.api.FabricLoader;
import www.wheelershigley.me.trade_experience.config.Configuration;
import www.wheelershigley.me.trade_experience.config.Configurations;

import java.io.File;

import static www.wheelershigley.me.trade_experience.TradeExperience.MOD_ID;

public class ConfigurationHelper {
    public static Configurations createTradeExperienceConfigurations() {
        final String configurationsFileName = (MOD_ID + ".properties").toLowerCase();
        final File configurationsFile = FabricLoader.getInstance().getConfigDir().resolve(configurationsFileName).toFile();
        final Configurations configurations = new Configurations(configurationsFile, configurationsFileName);

//        configurations.addConfiguration(
//            new Configuration<>(
//                "test",
//                true,
//                "test"
//            )
//        );

        return configurations;
    }
}
