package me.wheelershigley.www.solace_jobs.level;

import me.wheelershigley.www.solace_jobs.data.config.ConfigurationsCodec;
import me.wheelershigley.www.solace_jobs.data.players.JobsPlayerData;
import me.wheelershigley.www.solace_jobs.data.players.PlayersJobsDataCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

import java.time.Instant;
import java.util.*;

public class LevelHelper {
    public static double experienceToLevel(final double EXPERIENCE) {
        final double MULTIPLIER = ConfigurationsCodec.INSTANCE.getInstanceData().getData().level_multiplier();
        return (int)Math.sqrt(
            EXPERIENCE/MULTIPLIER
        );
    }

    public static double levelToExperience(final int LEVEL) {
        final double MULTIPLIER = ConfigurationsCodec.INSTANCE.getInstanceData().getData().level_multiplier();
        double result = MULTIPLIER/6.0;
        result *= (double)LEVEL;
        result *= ( 1.0 + (double)LEVEL );
        result *= ( 1.0 + 2.0*(double)LEVEL );
        return result;
    }

    public static double percentageToNextLevel(double totalExperience) {
        int level = (int)experienceToLevel(totalExperience);

        double levelExperience = totalExperience - levelToExperience(level);
        double requiredExperienceToNextLevel = levelToExperience(level+1);

        System.out.println(
            levelExperience + " / " + requiredExperienceToNextLevel
        );

        return Math.clamp(
            levelExperience / requiredExperienceToNextLevel,
            0.0, 1.0
        );
    }
}
