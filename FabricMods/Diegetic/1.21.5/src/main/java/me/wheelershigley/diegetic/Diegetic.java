package me.wheelershigley.diegetic;

import com.mojang.brigadier.arguments.StringArgumentType;
import me.wheelershigley.diegetic.command.DiegeticSuggestionProvider;
import me.wheelershigley.diegetic.config.Configuration;
import me.wheelershigley.diegetic.config.Configurations;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Diegetic implements ModInitializer {
    public static final String MOD_ID = "diegetic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Configurations configurations; static {
        final String configurationsFileName = (MOD_ID + ".properties").toLowerCase();
        final File configurationsFile = FabricLoader.getInstance().getConfigDir().resolve(configurationsFileName).toFile();
        configurations = new Configurations(configurationsFile, configurationsFileName);

        configurations.addConfiguration(
            new Configuration<>(
                "clock",
                true,
                "Clocks can display the [world] time."
            )
        );
        configurations.addConfiguration(
            new Configuration<>(
                "clock_real",
                false,
                "If clocks are enabled, clocks will display the server's (IRL) time."
            )
        );

        configurations.addConfiguration(
            new Configuration<>(
                "compass",
                true,
                "Compasses can provide absolute coordinates."
            )
        );
        configurations.addConfiguration(
            new Configuration<>(
                "lodestone_compass",
                true,
                "Compasses associated with lodestones will provide relative coordinates to their lodestone."
            )
        );
        configurations.addConfiguration(
            new Configuration<>(
                "recovery_compass",
                true,
                "Recovery-compasses will provide relate coordinates to last known death location."
            )
        );

        configurations.addConfiguration(
            new Configuration<>(
                "slime",
                true,
                "Slimeballs can inform if one's current chunk is a slime chunk."
            )
        );
    }

    @Override public void onInitialize() {
        configurations.reload();

        //reload command
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                dispatcher.register(
                    CommandManager.literal("diegetic")
                        .then(
                            CommandManager.argument(
                                    "sublet",
                                    StringArgumentType.string()
                                )
                                .suggests(new DiegeticSuggestionProvider() )
                                .executes(
                                    (context) -> {
                                        ServerPlayerEntity player = context.getSource().getPlayer();

                                        String message = "<"+MOD_ID+"> ";
                                        if(player != null && player.getPermissionLevel() == 0) {
                                            message += "§cinsufficient permissions";
                                        } else {
                                            message += "reloaded";
                                        }

                                        String sublet = StringArgumentType.getString(context, "sublet");
                                        if( sublet.equalsIgnoreCase("reload") ) {
                                            configurations.reload();

                                            if(player != null) {
                                                context.getSource().getPlayer().sendMessage( Text.literal(message) );
                                            }

                                            return 1;
                                        }
                                        return 0;
                                    }
                                )
                        )
                );
            }
        );
    }
}
