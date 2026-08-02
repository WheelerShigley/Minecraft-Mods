package me.wheelershigley.www.tree_in_a_forest.helpers;

import me.wheelershigley.www.tree_in_a_forest.TreeInAForest;
import me.wheelershigley.www.tree_in_a_forest.blacklist.Blacklist;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessagesHelper {
    public static boolean sendMessageInContext(
        @NotNull    CommandSourceStack source,
        @Nullable   ServerPlayer player,
        @NotNull    String key,
        @NotNull    Boolean wouldBeTellRaw,
                    Object... arguments
    ) {
        if( source.isPlayer() ) {
            return sendPlayerMessage(player, key, wouldBeTellRaw, arguments);
        } else {
            sendConsoleInfoTranslatableMessage(key, arguments);
            return true;
        }
    }

    public static void sendConsoleInfoTranslatableMessage(String key, Object... arguments) {
        TreeInAForest.LOGGER.info(
            Component.literal(
                Component.translatable(key, arguments).getString()
            ).getString()
        );
    }

    public static boolean sendPlayerMessage(ServerPlayer player, String key, boolean isTellRaw, Object... arguments) {
        if(player == null) {
            return false;
        }
        player.sendSystemMessage(
            Component.literal(
                Component.translatable(key, arguments).getString()
            ),
            isTellRaw
        );
        return true;
    }

    public static String getCommaSeperatedBlacklistedNames() {
        String blacklistedNames = ""; {
            StringBuilder blacklistedNamesBuilder = new StringBuilder();
            String[] names = Blacklist.getBlacklistedNames();
            int blacklistSize = names.length;

            blacklistedNamesBuilder.append('[');
            for(int index = 0; index < blacklistSize; index++) {
                blacklistedNamesBuilder.append( names[index] );
                if(index < blacklistSize-1) {
                    blacklistedNamesBuilder.append(", ");
                }
            }
            blacklistedNamesBuilder.append(']');
            blacklistedNames = blacklistedNamesBuilder.toString();
        }
        return blacklistedNames;
    }
}
