package me.wheelershigley.www.solace_crates.api;

import com.mojang.datafixers.util.Pair;

public class MathsHelper {
    private static final double ORDER_DATUM = 1.0 - Double.MIN_NORMAL;
    public static Pair<Double, Integer> notate(double number) {
        if(number == 0.0) {
            return new Pair<>(0.0, 0);
        }

        int order = 0;
        while(number < ORDER_DATUM) {
            order -= 1;
            number *= 10.0;
        }
        while(10.0*ORDER_DATUM < number) {
            order += 1;
            number /= 10.0;
        }

        return new Pair<>(number, order);
    }

    public static Pair<Double, Integer> boundNotation(
        Pair<Double, Integer> notation,
        int minimum_order,
        int maximum_order
    ) {
        if(maximum_order < minimum_order) {
            return notation;
        }

        // when too small, make larger
        while( notation.getSecond() < minimum_order ) {
            notation = new Pair<>(
                10.0 * notation.getFirst(),
                notation.getSecond() + 1
            );
        }
        // when too large, make smaller
        while( maximum_order < notation.getSecond() ) {
            notation = new Pair<>(
                0.10 * notation.getFirst(),
                notation.getSecond() - 1
            );
        }

        return notation;
    }

    public static double percentageRound(double chance) {
        return Math.round(100.0*chance)/100.0;
    }

    public static String getHumanReadablePercentage(double chance) {
        Pair<Double, Integer> notatedChance = notate(100.0*chance);

        //force to be fixed-point
        while( 0 < notatedChance.getSecond() ) {
            notatedChance = new Pair<>(
                10.0 * notatedChance.getFirst(),
                notatedChance.getSecond() - 1
            );
        }

        chance = percentageRound( notatedChance.getFirst() );
        int order = notatedChance.getSecond();
        double remainder = percentageRound(chance % 1);
        chance = (int)chance;

        //when rounded up, re-adjust
        if(order < 0 && 10.0 <= chance) {
            chance /= 10.0;
            order += 1;
        }

        StringBuilder stringBuilder = new StringBuilder();
        if(remainder == 0.0) {
            stringBuilder.append(
                Integer.toString( (int)chance )
            );
        } else {
            stringBuilder.append(
                percentageRound(chance +remainder)
            );
        }

        stringBuilder.append('%');
        if(order < 0) {
            stringBuilder.append('e').append(order);
        }

        return stringBuilder.toString();
    }
}
