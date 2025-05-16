package me.wheelershigley.tree_in_a_forest.mixin;

import me.wheelershigley.tree_in_a_forest.TreeInAForest;
import me.wheelershigley.tree_in_a_forest.blacklist.Blacklist;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
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
        WorldGenerationProgressListener worldGenerationProgressListener,
        CallbackInfo ci
    ) {
        TreeInAForest.updateServerTicking();
        Blacklist.initializeBlackList(onlineMode);
        sendBlacklistedUsersMessage();
    }

    private static void sendBlacklistedUsersMessage() {
        int blacklistSize = 0;
        String blacklistedNames = ""; {
            StringBuilder blacklistedNamesBuilder = new StringBuilder();
            String[] names = Blacklist.getBlacklistedNames();
            blacklistSize = names.length;
            for(int index = 0; index < blacklistSize; index++) {
                blacklistedNamesBuilder.append( names[index] );
                if(index < blacklistSize-1) {
                    blacklistedNamesBuilder.append(", ");
                }
            }
            blacklistedNames = blacklistedNamesBuilder.toString();
        }

        switch(blacklistSize) {
            case 0: {
                sendConsoleInfoTranslatableMessage("tree_in_a_forest.text.empty_blacklist");
                break;
            }
            case 1: {
                sendConsoleInfoTranslatableMessage(
                    "tree_in_a_forest.text.single_blacklist",
                    blacklistedNames
                );
                break;
            }
            default: {
                sendConsoleInfoTranslatableMessage(
                        "tree_in_a_forest.text.blacklist",
                        blacklistedNames
                );
                break;
            }
        }
    }
}
