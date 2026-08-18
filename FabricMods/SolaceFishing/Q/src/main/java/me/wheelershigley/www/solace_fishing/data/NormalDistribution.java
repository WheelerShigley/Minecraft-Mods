package me.wheelershigley.www.solace_fishing.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.RandomSource;

import org.apache.commons.math3.special.Erf;
import static java.lang.Math.sqrt;

public record NormalDistribution(double mean, double standard_deviation) {
    // For this Normal-Distribution, return its value at some input, x
    private static final double NORMAL_CONSTANT = 1.0/ sqrt(2.0*Math.PI);
    public double evaluate(double x) {
        double product_accumulator = NORMAL_CONSTANT;
        product_accumulator /= standard_deviation;

        double power = x - mean;
        power *= -power;
        power /= 2.0*standard_deviation*standard_deviation;
        product_accumulator *= Math.pow(Math.E, power);

        return product_accumulator;
    }

    /* Cumulative-Density-Function (CDF) of the Probability-Distribution-Function (PDF),
     * normal distribution, as the antiderivative between an infinite bound and
     * an arbitrary position can be found as 0.5*erf(p-mu/(s*root-2)+0.5
     */
    public double roll(RandomSource random) {
        double percentile = random.nextDouble();
        return 0.5 + 0.5 * Erf.erf(
            (percentile - mean) / (sqrt(2) * standard_deviation)
        );
    }

    /* This CDF is of the triple-PDF composed of the normal PDF and its reflections across some bounds.
     * The sum-antiderivative of the PDF and its reflections give an error-function sum, as below
     */
    public double inverseDoubleBoundedRoll(RandomSource random, Pair<Double, Double> bounds) {
        double minimum = bounds.getFirst();
        double maximum = bounds.getSecond();
        final double COMMON_DENOMINATOR = sqrt(2) * this.standard_deviation;

        double percentile = random.nextDouble();
        return 0.5 * (
            1.0 +
            Erf.erf(
                (percentile - this.mean)
                / COMMON_DENOMINATOR
            ) +
            Erf.erf(
                (percentile + this.mean - 2*minimum)
                / COMMON_DENOMINATOR
            ) +
            Erf.erf(
                (percentile + this.mean - 2*maximum)
                / COMMON_DENOMINATOR
            )
        );
    }

    // Bound-Normalized CDF for a Normal-Distribution
    public double simpleBoundRoll(RandomSource random, final Pair<Double, Double> bounds) {
        final double minimum = bounds.getFirst();
        final double maximum = bounds.getSecond();
        final double percentile = random.nextDouble();

        final double COMMON_DENOMINATOR = sqrt(2) * this.standard_deviation;
        final double lowerBoundIntegration = Erf.erf( (minimum - this.mean) / COMMON_DENOMINATOR );
        final double upperBoundIntegration = Erf.erf( (maximum - this.mean) / COMMON_DENOMINATOR );

        double test = Erf.erfInv(
            upperBoundIntegration * percentile +
            lowerBoundIntegration * (1.0 - percentile)
        );
        return this.mean + (
                COMMON_DENOMINATOR * test
        );
    }

}
