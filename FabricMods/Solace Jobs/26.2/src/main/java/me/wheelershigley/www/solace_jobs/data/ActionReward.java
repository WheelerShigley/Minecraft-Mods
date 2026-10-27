package me.wheelershigley.www.solace_jobs.data;

import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.players.JobsPlayerData;
import me.wheelershigley.www.solace_jobs.data.players.PlayersJobsDataCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.HashMap;

public record ActionReward(
    double experience,
    double job_experience,
    double currency
) {
    //TODO: revise
    public void give(
        ServerPlayer player,
        JobDefinition job,
        RandomSource random
    ) {
        giveExperience(player, random);
        giveJobExperience(player, job);
        //TODO: give currency
        //TODO: give job experience
    }

    private void giveExperience(ServerPlayer player, RandomSource random) {
        int amount = (int)experience;

        //any remainder will be added sometimes, to approximate that amount
        double remainder = experience%1;
        if(
            0 < remainder
            && random.nextDouble() <= remainder
        ) {
            amount++;
        }

        player.giveExperiencePoints(amount);
    }

    private void giveJobExperience(
        ServerPlayer player,
        JobDefinition job
    ) {
        JobsPlayerData playerData = PlayersJobsDataCodec.DATA.get( player.getUUID() );
        if(playerData == null) {
            playerData = new JobsPlayerData( new HashMap<>() );
            PlayersJobsDataCodec.DATA.set( player.getUUID(), playerData);
        }

        double experience = playerData.experiences().getOrDefault(job.identifier, 0.0);
        experience += this.experience;
        playerData.experiences().put(job.identifier, experience);
    }
}
