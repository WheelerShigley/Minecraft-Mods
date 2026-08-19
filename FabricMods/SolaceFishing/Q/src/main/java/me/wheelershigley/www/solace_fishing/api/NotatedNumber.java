package me.wheelershigley.www.solace_fishing.api;

import java.util.Collections;
import java.util.HashMap;

public class NotatedNumber {
    private double number;
    private int order;

    public NotatedNumber(double number, int order) {
        this.number = number;
        this.order = order;
    }

    private static final double ORDER_DATUM = 1.0 - Double.MIN_NORMAL;
    public NotatedNumber(double number) {
        int order = 0;

        while( 10.0*ORDER_DATUM < Math.abs(number) ) {
            number /= 10.0;
            order++;
        }

        if(number == 0.0) {
            this.number = number;
            this.order = order;
            return;
        }

        while( Math.abs(number) < ORDER_DATUM) {
            number *= 10.0;
            order--;
        }

        this.number = number;
        this.order = order;
    }

    private static final HashMap<Integer, String> SI_UNITS; static {
        SI_UNITS = new HashMap<>();

        SI_UNITS.put(+30, "Q" );
        SI_UNITS.put(+27, "R" );
        SI_UNITS.put(+24, "Y" );
        SI_UNITS.put(+21, "Z" );
        SI_UNITS.put(+18, "E" );
        SI_UNITS.put(+15, "P" );
        SI_UNITS.put(+12, "T" );
        SI_UNITS.put( +9, "G" );
        SI_UNITS.put( +6, "M" );
        SI_UNITS.put( +3, "k" );
        //SI_UNITS.put( +2, "h" );
        //SI_UNITS.put( +1, "da");
        SI_UNITS.put(  0, ""  );
        //SI_UNITS.put( -1, "d" );
        SI_UNITS.put( -2, "c" );
        SI_UNITS.put( -3, "m" );
        SI_UNITS.put( -6, "μ" );
        SI_UNITS.put( -9, "n" );
        SI_UNITS.put(-12, "p" );
        SI_UNITS.put(-15, "f" );
        SI_UNITS.put(-18, "a" );
        SI_UNITS.put(-21, "z" );
        SI_UNITS.put(-24, "y" );
        SI_UNITS.put(-27, "r" );
        SI_UNITS.put(-30, "q" );
    }
    public String setOrderAndGetPrefix() {
        int minimum = Collections.min( SI_UNITS.keySet() );
        int maximum = Collections.max( SI_UNITS.keySet() );

        //ensure number is in bounds
        while(this.order < minimum) {
            number /= 10.0;
            order++;
        }
        while(maximum < this.order) {
            number *= 10.0;
            order--;
        }

        // assumes that zero is in the keys
        while( !SI_UNITS.containsKey(this.order) ) {
            if(order < 0) {
                number /= 10.0;
                order++;
            } else {
                number *= 10.0;
                order--;
            }
        }

        return SI_UNITS.get(this.order);
    }

    public double getNumber() {
        return this.number;
    }
    public int getOrder() {
        return this.order;
    }

    public double getRaw() {
        return this.number * Math.pow(10.0, this.order);
    }
}
