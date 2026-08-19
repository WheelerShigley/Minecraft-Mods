package me.wheelershigley.www.solace_fishing.api.statistics;

public class UniformDistribution extends Distribution {
    private final double value;

    public UniformDistribution(double value) {
        this.value = value;
    }

    @Override
    public double get(double percentile) {
        return value;
    }

    @Override
    public boolean isInBounds(double value) {
        return true;
    }
}
