package me.wheelershigley.unlimited_anvil.helpers;

public class ExperienceHelper {
    public static int levelsToOrbCount(double level) {
        /*
        level2 + 6 × level (at levels 0–16)
        2.5 × level2 – 40.5 × level + 360 (at levels 17–31)
        4.5 × level2 – 162.5 × level + 2220 (at levels 32+)
         */
        if(level <= 16.0) {
            return (int)Math.ceil(level*level + 6*level);
        }
        if(32.0 <= level) {
            return (int)Math.ceil(4.5*level*level - 162.5*level + 2220.0);
        }
        return (int)Math.ceil(2.5*level*level - 40.5*level + 360.0);
    }
    public static double orbCountToLevels(int orb_count) {
        /*
        -3+sqrt(total+9) (total 0 to 352)
        81/10+sqrt( (2/5)*(total-7839/40) ) (total 353-1507)
        325/18+sqrt( (2/9)*(total-54215/72) ) (totals 1508+)
         */
        if(orb_count <= 352) {
            return -3.0 + Math.sqrt(
                ( (double)orb_count ) + 9.0
            );
        }
        if(1508 <= orb_count) {
            return (325.0/18.0) + Math.sqrt(
                (2.0/9.0) * (  ( (double)orb_count )  -  (54215.0/72.0)  )
            );
        }
        return (81.0/10.0) + Math.sqrt(
            (2.0/5.0) * (  ( (double)orb_count )  -  (7839.0/40.0)  )
        );
    }
}
