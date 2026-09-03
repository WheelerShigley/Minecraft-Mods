package me.wheelershigley.www.solace_fishing.api.statistics;

public class UniformDistribution extends Distribution {
    private final double value;

    public UniformDistribution(double value) {
        this.value = value;
    }

    @Override
    public double getZValue(double percentile) {
        return value;
    }

    @Override
    public double getPercentile(double minimum, double maximum) {
        double difference = Math.abs(maximum - minimum);
        return 0.5 * value * difference;
    }

    @Override
    public boolean isInBounds(double value) {
        return true;
    }
}
