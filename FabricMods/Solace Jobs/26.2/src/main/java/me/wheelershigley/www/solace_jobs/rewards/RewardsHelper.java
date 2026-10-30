package me.wheelershigley.www.solace_jobs.rewards;

import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.jobs.JobsCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.Set;

public class RewardsHelper {
    public static boolean attemptReward(
        ServerPlayer serverPlayer,
        Identifier targetIdentifier,
        ActionType actionType,
        RandomSource randomSource
    ) {
        Set<JobDefinition> jobs = JobsCodec.DATA.getJobs(targetIdentifier);
        for(JobDefinition job : jobs) {
            ActionReward reward = job.getFirstReward(targetIdentifier, actionType);
            if(reward == null) {
                continue;
            }
            reward.give(serverPlayer, job, randomSource);
            return true;
        }
        return false;
    }
}
