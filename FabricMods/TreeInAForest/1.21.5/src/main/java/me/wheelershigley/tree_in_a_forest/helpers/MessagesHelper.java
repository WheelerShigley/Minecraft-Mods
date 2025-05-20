package me.wheelershigley.tree_in_a_forest.helpers;

import me.wheelershigley.tree_in_a_forest.TreeInAForest;
import me.wheelershigley.tree_in_a_forest.blacklist.Blacklist;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessagesHelper {
    public static boolean sendMessageInContext(
        @NotNull    ServerCommandSource source,
        @Nullable   ServerPlayerEntity player,
        @NotNull    String key,
        @NotNull    Boolean wouldBeTellRaw,
                    Object... arguments
    ) {
        if( source.isExecutedByPlayer() ) {
            return sendPlayerMessage(player, key, wouldBeTellRaw, arguments);
        } else {
            sendConsoleInfoTranslatableMessage(key, arguments);
            return true;
        }
    }

    public static void sendConsoleInfoTranslatableMessage(String key, Object... arguments) {
        TreeInAForest.LOGGER.info(
            Text.literal(
                Text.translatable(key, arguments).getString()
            ).getString()
        );
    }

    public static boolean sendPlayerMessage(ServerPlayerEntity player, String key, boolean isTellRaw, Object... arguments) {
        if(player == null) {
            return false;
        }
        player.sendMessage(
            Text.literal(
                Text.translatable(key, arguments).getString()
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
