package me.wheelershigley.www.solace_jobs.level;

import me.wheelershigley.www.solace_jobs.data.config.ConfigurationsCodec;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.intellij.lang.annotations.Identifier;

public class LevelHelper {
    public static int experienceToLevel(final double EXPERIENCE) {
        /* The function below is the inverse function of levelToExperience(LEVEL),
         * which resolved to (M/6)L(L+1)(1+2L), where M is the multiplier and L is the level:
         *
         * private a(x) = (  108xM^2 + sqrt(3)*sqrt( 3888(m^4)x^2 - m^6 )  )^(1/3)
         * private b(x) = m/( a(x)3^(1/3) ) + a(x)/( m3^(2/3) ) - 1
         * public  y(x) = b(x)/2
         * .
         */
        final double MULTIPLIER = ConfigurationsCodec.INSTANCE.getInstanceData().getData().level_multiplier();

        double _a = 0;
        _a += 108*MULTIPLIER*MULTIPLIER*EXPERIENCE;
        _a += Math.sqrt(3.0) * Math.sqrt(
            3888.0
            * Math.pow(EXPERIENCE, 2)
            * Math.pow(MULTIPLIER, 4)
            - Math.pow(MULTIPLIER, 6)
        );
        _a = Math.pow(_a, 1.0/3.0);

        double _b = 0;
        _b += MULTIPLIER/(
            Math.pow(3.0, 1.0/3.0) * _a
        );
        _b += _a/(
            Math.pow(3.0, 2.0/3.0)
            * MULTIPLIER
        );
        _b -= 1;

        return (int)( _b/2.0 );
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
        int level = experienceToLevel(totalExperience);

        double levelExperience = totalExperience - levelToExperience(level);
        double requiredExperienceToNextLevel = levelToExperience(level+1) - levelToExperience(level);

        return Math.clamp(
            levelExperience / requiredExperienceToNextLevel,
            0.0, 1.0
        );
    }

    public static void notifyPlayerOfLevel(
        ServerPlayer serverPlayer,
        JobDefinition job,
        int level
    ) {
        serverPlayer.sendSystemMessage(
            Component.translatable(
                "solace_jobs.level_up",
                Component.translatable( job.getTranslationKey() ),
                level
            )
        );
    }
}
