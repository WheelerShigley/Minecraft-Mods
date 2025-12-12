package me.wheelershigley.tree_in_a_forest.mixin;

import me.wheelershigley.tree_in_a_forest.TreeInAForest;
import me.wheelershigley.tree_in_a_forest.blacklist.Blacklist;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.wheelershigley.tree_in_a_forest.helpers.MessagesHelper.sendConsoleInfoTranslatableMessage;

@Mixin(MinecraftServer.class)
public abstract class StartingActionsMixin {
    @Shadow
    private boolean onlineMode;

    @Inject(
        method = "createWorlds",
        at = @At("TAIL")
    )
    private void createWorlds(
        CallbackInfo ci
    ) {
        TreeInAForest.updateServerTicking();
        Blacklist.initializeBlackList(onlineMode);
        sendBlacklistedUsersMessage();
    }

    private static void sendBlacklistedUsersMessage() {
        int blacklistSize = Blacklist.nameBlacklist.size() + Blacklist.configEntryBlacklist.size();

        String key = "";
        String players = "";
        switch(blacklistSize) {
            case 0: {
                key = "tree_in_a_forest.text.empty_blacklist";
                players = "0 players";
                break;
            }
            case 1: {
                key = "tree_in_a_forest.text.single_blacklist";
                players = "1 player";
                break;
            }
            default: {
                key = "tree_in_a_forest.text.blacklist";
                players = Integer.toString(blacklistSize)+" players";
            }
        }
        sendConsoleInfoTranslatableMessage(key, players);
    }
}
