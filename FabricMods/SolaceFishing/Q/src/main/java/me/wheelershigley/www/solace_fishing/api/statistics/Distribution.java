package me.wheelershigley.www.solace_fishing.api.statistics;

public abstract class Distribution {
    public abstract double getZValue(double percentile);
    public abstract double getPercentile(double min, double max);
    public abstract boolean isInBounds(double value);
}
