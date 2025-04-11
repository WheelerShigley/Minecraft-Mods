package me.wheelershigley.diegetic.items;

import me.wheelershigley.diegetic.Diegetic;
import me.wheelershigley.diegetic.helper.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Calendar;

public class Clock {
    public static void use(ServerPlayerEntity player) {
        if( !(boolean)Diegetic.configurations.getConfiguration("clock").getValue() ) {
            return;
        }

        boolean worldIsNatural = player.getWorld().getDimension().natural();
        int time;
        if(worldIsNatural) {
            time = (int)player.getWorld().getTimeOfDay();
        } else {
            time = player.getWorld().random.nextBetween(0, 24000);
        }

        StringBuilder timeBuilder = new StringBuilder();
        if( (boolean)Diegetic.configurations.getConfiguration("clock_real").getValue() ) {
            timeBuilder.append("§e");
            timeBuilder.append( Calendar.getInstance().getTime().toString() );
        } else {
            timeBuilder.append( worldIsNatural ? "§e" : "§0" );
            timeBuilder.append(
                convertToTime(
                    time,
                    player.getWorld().getTickManager().getTickRate()
                )
            );
        }

        MessageHelper.sendMessage(player, timeBuilder.toString() );
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
        if( number.isBlank() || number.isEmpty() ) {
            return "00";
        }
        if(number.length() < 2) {
            return "0"+number;
        }
        return number;
    }
}
