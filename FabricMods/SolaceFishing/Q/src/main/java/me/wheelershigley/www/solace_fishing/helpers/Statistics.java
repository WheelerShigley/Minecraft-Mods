package me.wheelershigley.www.solace_fishing.helpers;

import java.util.Random;

public class Statistics {
    /**
     * Computes the factorial of a non-negative integer using an iterative approach.
     * @author JavaThinking.com
     * @param n the non-negative integer to compute the factorial of
     * @return the factorial of n as a long
     * @throws IllegalArgumentException if n is negative
     */
    public static long factorial(int n) {
        if(n < 0) {
            throw new IllegalArgumentException("Factorial is not defined for negative numbers: " + n);
        }
        long result = 1;
        for(int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static double errorFunction(double x) {
        double accumulator = 0;

        // SUM{n=0, infinity}( ... )
        double current = 0;
        for(int n = 0; n < 5; n++) {
            int order = 2*n+1;

            /* (-1)^n */ {
                current = 1;
                if(n%2 == 0) {
                    current = -1;
                }
            }
            /* x^(2n+1) */ {
                current += Math.pow(x, order);
            }
            /* 1/n! */ {
                current /= factorial(n);
            }
            /* 1/(2n+1) */ {
                current /= order;
            }
            accumulator += current;
        }

        return accumulator;
    }

    private static final double NORMAL_CONSTANT = 1.0/Math.sqrt(2.0*Math.PI);
    public static double normal(double mean, double standard_deviation, double x) {
        double product_accumulator = NORMAL_CONSTANT;
        product_accumulator /= standard_deviation;

        double power = x - mean;
        power *= -power;
        power /= 2.0*standard_deviation*standard_deviation;
        product_accumulator *= Math.pow(Math.E, power);

        return product_accumulator;
    }
}
