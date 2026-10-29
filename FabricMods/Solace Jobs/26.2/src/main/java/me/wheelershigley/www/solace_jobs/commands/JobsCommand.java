package me.wheelershigley.www.solace_jobs.commands;

import me.wheelershigley.www.solace_jobs.menus.JobsGUI;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class JobsCommand {
    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                dispatcher
                    .register(Commands.literal("jobs")
                    .executes(
                        (context) -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            JobsGUI menu = new JobsGUI(player);
                            return (menu.open() ? 0 : 1);
                        }
                    )
                );
            }
        );
    }
}
