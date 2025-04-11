package me.wheelershigley.diegetic.imeplementations;

import me.wheelershigley.diegetic.Diegetic;
import me.wheelershigley.diegetic.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;

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

//        MessageHelper.sendMessage(
//            player,
//            prefix+"Canon Time:   "+ Calendar.getInstance().getTime().toString()
//        );
//        MessageHelper.sendMessage(
//            player,
//            prefix+"Server Time: "+ convertToTime( player.getWorld().getTime() )
//        );
        String namedTime = worldIsNatural ? "§e" : "§0";
        namedTime += convertToTime(time%MINECRAFT_DAY_IN_TICKS);
        MessageHelper.sendMessage(player, namedTime);
    }

    private static final int SECOND_IN_TICKS = 20; //assumed 20tps
    private static final int MINUTE_IN_TICKS = SECOND_IN_TICKS*60;
    private static final int MINECRAFT_DAY_IN_TICKS = MINUTE_IN_TICKS*20;

    private static String convertToTime(int sum_time) {
        double percentage = ( (double)sum_time )/( (double)MINECRAFT_DAY_IN_TICKS );

        StringBuilder timeBuilder = new StringBuilder();

        int hour = (int)(24.0*percentage);
        timeBuilder.append(  forceLeadingZero( Integer.toString(hour) )  ).append(':');
        percentage -= ( (double)hour )/24.0;

        int minute = (int)(60.0*24.0*percentage);
        timeBuilder.append(  forceLeadingZero( Integer.toString(minute) )  ).append(':');
        percentage -= ( (double)minute )/(24.0*60.0);

        int second = (int)(60.0*60.0*24.0*percentage);
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
