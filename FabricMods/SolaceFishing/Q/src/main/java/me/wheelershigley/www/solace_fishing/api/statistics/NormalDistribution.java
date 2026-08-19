package me.wheelershigley.www.solace_fishing.api.statistics;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.RandomSource;

import org.apache.commons.math3.special.Erf;
import org.spongepowered.asm.mixin.Unique;

import static java.lang.Math.sqrt;

public class NormalDistribution extends Distribution {
    private final double mean, standard_deviation;

    public NormalDistribution(double mean, double standard_deviation) {
        this.mean = mean;
        this.standard_deviation = standard_deviation;
    }

    // For this Normal-Distribution, return its value at some input, x
    private static final double NORMAL_CONSTANT = 1.0/ sqrt(2.0*Math.PI);
    @Override
    public double get(double x) {
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
    @Unique
    public double roll(RandomSource random) {
        double percentile = random.nextDouble();
        return 0.5 + 0.5 * Erf.erf(
            (percentile - mean) / (sqrt(2) * standard_deviation)
        );
    }

    // Bound-Normalized CDF for a Normal-Distribution
    @Unique
    public double simpleBoundRoll(RandomSource random, final Pair<Double, Double> bounds) {
        final double minimum = bounds.getFirst();
        final double maximum = bounds.getSecond();
        final double percentile = random.nextDouble();

        final double COMMON_DENOMINATOR = sqrt(2) * this.standard_deviation;
        final double lowerBoundIntegration = Erf.erf( (minimum - this.mean) / COMMON_DENOMINATOR );
        final double upperBoundIntegration = Erf.erf( (maximum - this.mean) / COMMON_DENOMINATOR );

        return this.mean + (
            COMMON_DENOMINATOR * Erf.erfInv(
                upperBoundIntegration * percentile +
                lowerBoundIntegration * (1.0 - percentile)
            )
        );
    }

    @Override
    public boolean isInBounds(double value) {
        // Anything outside of two standard-deviations is considered out-of-bounds
        double deviation = (value - mean) / standard_deviation;
        if(deviation < -2.0 || 2.0 < deviation) {
            return false;
        }
        return true;
    }

    public double getMean() {
        return mean;
    }
    public double getStandardDeviation() {
        return standard_deviation;
    }

}
