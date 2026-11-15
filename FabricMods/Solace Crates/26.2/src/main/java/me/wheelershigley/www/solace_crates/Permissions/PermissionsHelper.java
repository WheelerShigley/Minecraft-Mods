package me.wheelershigley.www.solace_crates.Permissions;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public class PermissionsHelper {
    public static boolean hasCommandPermission(CommandSourceStack source) {
        return source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR);
    }
    public static boolean hasCommandPermission(ServerPlayer player) {
        return player.permissions().hasPermission(Permissions.COMMANDS_MODERATOR);
    }
}
