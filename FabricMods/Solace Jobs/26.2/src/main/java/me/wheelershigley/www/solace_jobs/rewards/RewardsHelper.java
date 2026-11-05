package me.wheelershigley.www.solace_jobs.rewards;

import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import me.wheelershigley.www.solace_jobs.level.LevelBarHandler;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class RewardsHelper {
    public static boolean attemptReward(
        ServerPlayer serverPlayer,
        Identifier targetIdentifier,
        ActionType actionType
    ) {
        Set<JobDefinition> jobs = JobsCodec.DATA.getJobsFor(targetIdentifier);
        for(JobDefinition job : jobs) {
            ActionReward reward = job.getFirstReward(targetIdentifier, actionType);
            if(reward == null) {
                continue;
            }
            reward.give(serverPlayer, job, serverPlayer.getRandom());
            LevelBarHandler.notifyJobLevel(
                serverPlayer,
                job,
                serverPlayer.level().getServer()
            );
            return true;
        }
        return false;
    }
}
