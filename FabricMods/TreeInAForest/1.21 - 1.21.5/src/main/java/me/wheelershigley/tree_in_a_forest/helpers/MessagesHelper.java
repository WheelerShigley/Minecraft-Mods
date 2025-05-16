package me.wheelershigley.tree_in_a_forest.helpers;

import me.wheelershigley.tree_in_a_forest.TreeInAForest;
import net.minecraft.text.Text;

public class MessagesHelper {
    public static void sendConsoleInfoTranslatableMessage(String key, Object... arguments) {
        TreeInAForest.LOGGER.info(
            Text.literal(
                Text.translatable(
                    key,
                    arguments
                ).getString()
            ).getString()
        );
    }
}
