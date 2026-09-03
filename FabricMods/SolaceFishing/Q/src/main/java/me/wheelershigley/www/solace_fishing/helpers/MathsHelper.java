package me.wheelershigley.www.solace_fishing.helpers;

import com.mojang.datafixers.util.Pair;

public class MathsHelper {
    private static final double ORDER_DATUM = 1.0 - Double.MIN_NORMAL;
    public static Pair<Double, Integer> notate(double number) {
        if(number == 0.0) {
            return new Pair<Double, Integer>(0.0, 0);
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

    public static Pair<Double, Integer> boundNotation(Pair<Double, Integer> notation, int minimum_order, int maximum_order) {
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
}
