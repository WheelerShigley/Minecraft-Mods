package me.wheelershigley.www.solace_fishing.data;

import me.wheelershigley.www.solace_fishing.helpers.Statistics;
import net.minecraft.util.RandomSource;

public record DistributionData(double mean, double standard_deviation) {
    public double roll(RandomSource random) {
        double percentile = random.nextDouble();

        /* Cumulative-Density-Function of the Probability-Distribution-Function,
           normal distribution, as the antiderivative between an infinite bound and
           an arbitrary position can be found as 0.5*erf(p-mu/(s*root-2)+0.5
         */
        return 0.5 + 0.5 * Statistics.errorFunction(
            (percentile - mean) / (Math.sqrt(2.0) * standard_deviation)
        );
    }
}
