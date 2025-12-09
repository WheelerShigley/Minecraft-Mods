package me.wheelershigley.www.diegetic.items;

import me.wheelershigley.www.diegetic.helper.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.Calendar;

import static me.wheelershigley.www.diegetic.gamerules.DiegeticGameRules.CLOCK_DISPLAYS_TIME;
import static me.wheelershigley.www.diegetic.gamerules.DiegeticGameRules.CLOCK_USES_REAL_TIME;
import static me.wheelershigley.www.diegetic.helper.ServerWorldHelper.getBooleanGameRuleValue;

public class Clock {
    public static void use(ServerPlayerEntity player) {
        boolean clockDisplaysTime = getBooleanGameRuleValue(player.getEntityWorld(), CLOCK_DISPLAYS_TIME);
        boolean clockCanDisplaysServerTime = getBooleanGameRuleValue(player.getEntityWorld(), CLOCK_USES_REAL_TIME);
        if(!clockDisplaysTime) {
            return;
        }

        World playerWorld = player.getEntityWorld();
        boolean isNatural = !playerWorld.getDimension().hasFixedTime();
        int time;
        if(isNatural) {
            time = (int)playerWorld.getTimeOfDay();
        } else {
            time = playerWorld.random.nextBetween(0, 24000);
        }

        if(clockCanDisplaysServerTime) {
            MessageHelper.sendMessage(
                player,
                "diegetic.text.clock.natural_time",
                Calendar.getInstance().getTime().toString()
            );
        } else {
            MessageHelper.sendMessage(
                player,
                "diegetic.text.clock." + (isNatural ? "natural_time" : "unnatural_time"),
                convertToTime(
                    time,
                    playerWorld.getTickManager().getTickRate()
                )
            );
        }
    }

    private static String convertToTime(int sum_time, float tps) {
        //input validation
        if(tps <= 0.0) { tps = 20.0f; }
        if(sum_time < 0) { sum_time = 0; }

        float MINUTE_IN_TICKS = tps*60.0f;
        float MINECRAFT_DAY_IN_TICKS = MINUTE_IN_TICKS*20.0f;
        sum_time %= (int)MINECRAFT_DAY_IN_TICKS;

        float percentage = ( (float)sum_time )/MINECRAFT_DAY_IN_TICKS;

        StringBuilder timeBuilder = new StringBuilder();

        int hour = (int)(24.0*percentage);
        timeBuilder.append(  forceLeadingZero( Integer.toString(hour) )  ).append(':');
        percentage -= ( (float)hour )/24.0f;

        int minute = (int)(60.0*24.0*percentage);
        timeBuilder.append(  forceLeadingZero( Integer.toString(minute) )  ).append(':');
        percentage -= ( (float)minute )/(24.0f*60.0f);

        int second = (int)(60.0f*60.0f*24.0f*percentage);
        timeBuilder.append(  forceLeadingZero( Integer.toString(second) )  );

        return timeBuilder.toString();
    }

    private static String forceLeadingZero(String number) {
        if( number.isBlank() ) {
            return "00";
        }
        if(number.length() < 2) {
            return "0"+number;
        }
        return number;
    }
}
