package me.wheelershigley.charged.helper;

import me.wheelershigley.charged.config.Configuration;
import me.wheelershigley.charged.config.Configurations;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

import static me.wheelershigley.charged.Charged.MOD_ID;

public class ConfigurationsHelper {
    public static Configurations getConfiguations() {
        final String configurationsFileName = (MOD_ID + ".properties").toLowerCase();
        final File configurationsFile = FabricLoader.getInstance().getConfigDir().resolve(configurationsFileName).toFile();
        final Configurations configurations = new Configurations(configurationsFile, configurationsFileName);

        configurations.addConfiguration(
            new Configuration<>(
                "enablePlayerHeadDrops",
                true,
                "When enabled, players will be included as entities which drop heads."
            )
        );
        configurations.addConfiguration(
            new Configuration<>(
                "PlayerHeadsUseSkins",
                true,
                "When enabled, dropped player-heads will use the victim's texture; otherwise, it will be the default (Steve) texture."
            )
        );
//        configurations.addConfiguration(
//            new Configuration<>(
//                "PlayerHeadTextureWashing",
//                true,
//                "When enabled, player-head textures can be washed off with water, in a Cauldron, to the default (Steve) texture."
//            )
//        );
        configurations.addConfiguration(
            new Configuration<>(
                "MaximumDropsPerChargedCreeper",
                -1L,
                new String[]{
                    "Set some maximum amount of heads dropped by an individual Charged Creeper's explosion.",
                    "When negative, there will be no limit!"
                }
            )
        );

        return configurations;
    }
}
