package www.wheelershigley.me.trade_experience;

//import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import www.wheelershigley.me.trade_experience.helpers.ExperienceHelper;

import java.util.UUID;

import static www.wheelershigley.me.trade_experience.helpers.ExperienceHelper.*;
import static www.wheelershigley.me.trade_experience.helpers.MessageHelper.*;

public class Trade {
    private final UUID sender;
    private final UUID receiver;
    private final World world;
    private final long time;

    public Trade(UUID sender, UUID receiver, World world, long time) {
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
    public World getWorld() {
        return world;
    }
    public long getTime() {
        return time;
    }

//    @Deprecated
//    public ActionResult execute(MinecraftServer server, int amount) {
//        ServerPlayerEntity serverSender = server.getPlayerManager().getPlayer(sender);
//        ServerPlayerEntity serverReceiver = server.getPlayerManager().getPlayer(receiver);
//        if(serverSender == null || serverReceiver == null) {
//            return null;
//        }
//
//        int available_funds = levelToPoints(serverSender.experienceLevel) + getExperiencePoints(serverSender);
//        if(available_funds < amount) {
//            return ActionResult.FAIL;
//        }
//
//        boolean successfullyTaken = takeExperience(serverSender, amount);
//        if(!successfullyTaken) {
//            return ActionResult.FAIL;
//        }
//        giveExperience(serverReceiver, amount);
//
//        return ActionResult.SUCCESS_SERVER;
//    }

    public static void performTrade(ServerPlayerEntity giver, ServerPlayerEntity taker, int amount) {
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

        int maximum_experience = levelToPoints(giver.experienceLevel) + ExperienceHelper.getExperiencePoints(giver);
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
        giver.playSoundToPlayer(
            SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW,
            SoundCategory.PLAYERS,
            1.0f,
            1.2f
        );

        ExperienceHelper.giveExperience(taker, amount);
        taker.playSoundToPlayer(
            SoundEvents.ENTITY_PLAYER_LEVELUP,
            SoundCategory.PLAYERS,
            1.0f,
            1.2f
        );

        sendSentFundsChatMessage(giver, taker, Integer.toString(amount) );
        sendReceivalChatMessage( taker, giver, Integer.toString(amount) );
    }
}
