package me.wheelershigley.charged;

import com.mojang.brigadier.arguments.StringArgumentType;
import me.wheelershigley.charged.command.ChargedSuggestionProvider;
import me.wheelershigley.charged.config.Configuration;
import me.wheelershigley.charged.config.Configurations;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Charged implements ModInitializer {
    public static final String MOD_ID = "Charged";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Configurations configurations; static {
        final String configurationsFileName = (MOD_ID + ".properties").toLowerCase();
        final File configurationsFile = FabricLoader.getInstance().getConfigDir().resolve(configurationsFileName).toFile();
        configurations = new Configurations(configurationsFile, configurationsFileName);

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
    }

    @Override
    public void onInitialize() {
        configurations.reload();

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                dispatcher.register(
                    CommandManager.literal("charged")
                        .then(
                            CommandManager.argument(
                                "sublet",
                                StringArgumentType.string()
                            )
                            .suggests(new ChargedSuggestionProvider() )
                            .executes(
                                (context) -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();

                                    String message = "<"+MOD_ID+"> ";
                                    if(player != null && player.getPermissionLevel() == 0 ) {
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
