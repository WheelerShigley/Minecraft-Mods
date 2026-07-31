package me.wheelershigley.www.trade_experience;

import me.wheelershigley.www.trade_experience.helpers.ExperienceHelper;

import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

import static me.wheelershigley.www.trade_experience.helpers.ExperienceHelper.*;
import static me.wheelershigley.www.trade_experience.helpers.MessageHelper.*;

public class Trade {
    private final UUID sender;
    private final UUID receiver;
    private final Level world;
    private final long time;

    public Trade(UUID sender, UUID receiver, Level world, long time) {
        this.sender = sender;
        this.receiver = receiver;
        this.world = world;
        this.time = time;
    }

    public UUID getSender() {
        return sender;
    }
    public UUID getReciever() {
        return receiver;
    }
    public Level getWorld() {
        return world;
    }
    public long getTime() {
        return time;
    }

    public static void performTrade(ServerPlayer giver, ServerPlayer taker, int amount) {
        if(giver == null) {
            return;
        }
        if(taker == null) {
            sendMessage(
                giver,
                "trade_experience.text.receiver_offline",
                false,
                TradeExperience.experienceName
            );
            return;
        }

        int maximum_experience = getExperiencePoints(giver);
        if(maximum_experience < amount) {
            sendMessage(
                giver,
                "trade_experience.text.insufficient_funds",
                false,
                TradeExperience.experienceName,
                Integer.toString(maximum_experience),
                Integer.toString(amount)
            );
            return;
        }

        if( !ExperienceHelper.takeExperience(giver, amount) ) {
            sendMessage(
                giver,
                "trade_experience.text.send_failure",
                false,
                TradeExperience.experienceName
            );
            return;
        }
        giver.playSound(
            SoundEvents.EXPERIENCE_BOTTLE_THROW,
            1.0f,
            1.2f
        );

        ExperienceHelper.giveExperience(taker, amount);
        taker.playSound(
            SoundEvents.PLAYER_LEVELUP,
            1.0f,
            1.2f
        );

        sendSentFundsChatMessage(giver, taker, Integer.toString(amount) );
        sendReceivalChatMessage( taker, giver, Integer.toString(amount) );
    }
}
