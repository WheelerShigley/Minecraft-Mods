package me.wheelershigley.diegetic.imeplementations;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Calendar;

public class Clock {
    public static void use(ServerPlayerEntity player, ItemStack clock) {
        String prefix = "<§e"+ clock.getName().getString() +"§r> ";

        player.sendMessage(
            Text.literal(
                prefix+"Canon Time:   "+ Calendar.getInstance().getTime().toString()
            )
        );
        player.sendMessage(
            Text.literal(
                prefix+"Server Time: "+ convertToTime( player.getWorld().getTime() )
            )
        );
        player.sendMessage(
            Text.literal(
                prefix+"World Time:   "+ convertToTime( player.getWorld().getTimeOfDay() % MINECRAFT_DAY_IN_TICKS )
            )
        );
    }

    private static final int SECOND_IN_TICKS = 20; //assumed 20tps
    private static final int MINUTE_IN_TICKS = SECOND_IN_TICKS*60;
    private static final int HOUR_IN_TICKS = MINUTE_IN_TICKS*60;
    private static final int DAY_IN_TICKS = HOUR_IN_TICKS*60;
    private static final int YEAR_IN_TICKS = DAY_IN_TICKS*365;

    private static final int MINECRAFT_DAY_IN_TICKS = MINUTE_IN_TICKS*20;

    private static String convertToTime(long _time) {

        int[] times = new int[6];

        //Calculate times as {years, days, hours, minutes, seconds, ticks}
        int time = (int)_time;

        times[0] = time/YEAR_IN_TICKS;
        time -= times[0]*YEAR_IN_TICKS;
        times[1] = time/DAY_IN_TICKS;
        time -= times[1]*DAY_IN_TICKS;
        times[2] = time/HOUR_IN_TICKS;
        time -= times[2]*HOUR_IN_TICKS;
        times[3] = time/MINUTE_IN_TICKS;
        time -= times[3]*MINUTE_IN_TICKS;
        times[4] = time/SECOND_IN_TICKS;
        times[5] = time % SECOND_IN_TICKS;

        //Write time as String
        String[] timeNames = new String[]{"Year", "Day", "Hour", "Minute", "Second", "Tick"};
        StringBuilder timeBuilder = new StringBuilder();

        int quantized_time; String quantizedName;
        for(int iterator = 0; iterator < Math.min(times.length, timeNames.length); iterator++) {
            quantized_time = times[iterator];
            quantizedName = timeNames[iterator];

            if(0 < quantized_time) {
                if( !timeBuilder.isEmpty() ) {
                    timeBuilder.append(", ");
                }

                timeBuilder
                        .append( Integer.toString(quantized_time) )
                        .append(' ')
                        .append(quantizedName)
                ;
                //plural
                if(2 <= quantized_time) {
                    timeBuilder.append('s');
                }
            }
        }

        return timeBuilder.toString();
    }
}
