package me.wheelershigley.silktouchplus.mixins;

import com.mojang.brigadier.context.CommandContext;
import me.wheelershigley.silktouchplus.SilkTouchPlus;
import net.minecraft.server.command.GameRuleCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.wheelershigley.silktouchplus.helpers.MessageHelper.*;
import static me.wheelershigley.silktouchplus.registrations.GameRuleRegistrator.*;

@Mixin(GameRuleCommand.class)
public class RestartMessageMixin {
    @Inject(
        method = "executeSet",
            at = @At("TAIL")
    )
    private static < T extends GameRules.Rule<T> > void sendRestartRequiredMessageWhenSilktouchGamerule(
        CommandContext<ServerCommandSource> context,
        GameRules.Key<T> key,
        CallbackInfoReturnable<Integer> cir
    ) {
        if( ArrayUtils.contains(SILKTOUCH_GAMERULES, key) ) {
            if( !context.getSource().isExecutedByPlayer() ) {
                sendConsoleInfoTranslatableMessage("silktouchplus.text.restart_required");
                return;
            }

            ServerPlayerEntity player = context.getSource().getPlayer();
            if(player != null) {
                sendPlayerTranslatableMessage(player, false, "silktouchplus.text.restart_required");
            }
        }
    }
}
