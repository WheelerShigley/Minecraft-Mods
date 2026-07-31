package me.wheelershigley.www.trade_experience.helpers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ExperienceHelper {
    private static final int MAX_BALANCE = ~(0b11 << 30);

    public static int pointsToNextLevel(int level) {
        if(level < 0) {
            level = Math.abs(level);
        }

        if(0 <= level && level <= 15) {
            return 2*level+7;
        }
        if(15 < level && level <= 30) {
            return 5*level-38;
        }
        return 9*level-158;
    }

    public static int levelToPoints(int level) {
        if(level < 0) {
            level = -level;
        }
        double _level = level;
        double _squared_level = _level*_level;

        if(0 <= level && level <= 16) {
            return (int)( _squared_level + 6.0*_level );
        }
        if(16 < level && level <= 31) {
            return (int)( 2.5*_squared_level - 40.5*_level + 360.0 );
        }
        return (int)( 4.5*_squared_level - 162.5*_level + 2220.0 );
    }

    public static int pointsToLevel(int points) {
        if(points < 0) {
            points = Math.abs(points);
        }
        double _points = (double)points;

        if(0 <= points && points <= 352) {
            return (int)( Math.sqrt(_points+9.0)-3.0 );
        }
        if(352 < points && points <= 1507) {
            return (int)( 8.1 + Math.sqrt(0.4*_points-78.39) );
        }
        return (int)(   325.0/18.0   +   Math.sqrt(  2.0/9.0  *  (_points - 54215.0/72.0)  )   );
    }

    public static boolean takeExperience(ServerPlayer player, int amount) {
        if( player.isCreative() ) {
            return true;
        }

        int newPoints = getExperiencePoints(player) - amount;
        int newLevel = pointsToLevel(newPoints);
        newPoints -= levelToPoints(newLevel);

        player.setExperienceLevels(newLevel);
        player.setExperiencePoints(newPoints);
        return true;
    }

    public static void giveExperience(ServerPlayer player, int amount) {
        if( player.isCreative() ) {
            return;
        }

        int points = getExperiencePoints(player);
        points += amount;
        points = Math.min(points, MAX_BALANCE);

        int final_level = pointsToLevel(points);
        points -= levelToPoints(final_level);

        player.setExperienceLevels(final_level);
        player.setExperiencePoints(points);
    }

    public static int getExperiencePoints(Player player) {
        if( player.isCreative() ) {
            return MAX_BALANCE;
        }

        int levelXp = levelToPoints(player.experienceLevel);
        int currentLevelXp = Math.round(
            player.experienceProgress * (float)( player.getXpNeededForNextLevel() )
        );

        return levelXp + currentLevelXp;
    }
}