package me.wheelershigley.unlimited_anvil.helpers;

public class ExperienceHelper {
//    public static int pointsToNextLevel(int level) {
//        if(level < 0) {
//            level = Math.abs(level);
//        }
//
//        if(0 <= level && level <= 15) {
//            return 2*level+7;
//        }
//        if(15 < level && level <= 30) {
//            return 5*level-38;
//        }
//        return 9*level-158;
//    }

    public static int levelToPoints(int level) {
        if(level < 0) {
            level = Math.abs(level);
        }
        double _level = (double)level;

        if(0 <= level && level <= 16) {
            return (int)( _level*_level + 6.0*_level );
        }
        if(16 < level && level <= 31) {
            return (int)( 2.5*_level*_level - 40.5*_level + 360.0 );
        }
        return (int)( 4.5*_level*_level - 162.5*_level + 2220.0 );
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
}