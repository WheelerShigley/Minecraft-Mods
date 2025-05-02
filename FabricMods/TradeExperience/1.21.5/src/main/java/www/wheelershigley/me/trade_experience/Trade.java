package www.wheelershigley.me.trade_experience;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import java.util.UUID;

import static www.wheelershigley.me.trade_experience.helpers.ExperienceHelper.*;

public class Trade {
    public static final int COOLDOWN = 30 * 20; //30 seconds

    private UUID sender;
    private UUID receiver;
    private World world;
    private long time;

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

    public ActionResult execute(MinecraftServer server, int amount) {
        ServerPlayerEntity serverSender = server.getPlayerManager().getPlayer(sender);
        ServerPlayerEntity serverReceiver = server.getPlayerManager().getPlayer(receiver);
        if(serverSender == null || serverReceiver == null) {
            return null;
        }

        int available_funds = levelToPoints(serverSender.experienceLevel) + getExperiencePoints(serverSender);
        if(available_funds < amount) {
            return ActionResult.FAIL;
        }

        boolean successfullyTaken = takeExperience(serverSender, amount);
        if(!successfullyTaken) {
            return ActionResult.FAIL;
        }
        giveExperience(serverReceiver, amount);

        int test = levelToPoints(serverReceiver.experienceLevel)+getExperiencePoints(serverReceiver);
        return ActionResult.SUCCESS_SERVER;
    }
}
