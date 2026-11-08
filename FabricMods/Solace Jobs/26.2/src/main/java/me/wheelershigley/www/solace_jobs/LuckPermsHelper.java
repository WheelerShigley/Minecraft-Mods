package me.wheelershigley.www.solace_jobs;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class LuckPermsHelper {
    public static boolean hasPermission(CommandSourceStack source, final String PERMISSION) {
        if( !source.isPlayer() ) {
            return true;
        }

        ServerPlayer serverPlayer = source.getPlayer();
        if(serverPlayer == null) {
            return false;
        }

        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser( serverPlayer.getUUID() );
        if(user == null) {
            return false;
        }

        return user.getCachedData()
            .getPermissionData(
                QueryOptions.builder(QueryMode.CONTEXTUAL).build()
            )
            .getPermissionMap()
            .entrySet()
            .stream()
            .filter(Map.Entry::getValue)
            .map(Map.Entry::getKey)
            .anyMatch(
                permission -> permission.equals(PERMISSION)
            )
        ;
    }


    public static int getMaximumPermission(ServerPlayer serverPlayer, final String PREFIX) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser( serverPlayer.getUUID() );
        if(user == null) {
            return 1;
        }

        return user.getCachedData()
            .getPermissionData(
                QueryOptions.builder(QueryMode.CONTEXTUAL).build()
            )
            .getPermissionMap()
            .entrySet()
            .stream()
            .filter(Map.Entry::getValue)
            .map(Map.Entry::getKey)
            .filter(
                permission -> permission.startsWith(PREFIX)
            )
            .map(
                permission -> permission.substring( PREFIX.length() )
            )
            .mapToInt(
                value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch(NumberFormatException exception) {
                        return 0;
                    }
                }
            )
            .max()
            .orElse(0)
        ;
    }
}
