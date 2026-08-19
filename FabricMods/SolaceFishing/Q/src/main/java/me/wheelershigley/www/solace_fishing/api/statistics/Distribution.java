package me.wheelershigley.www.solace_fishing.api.statistics;

public abstract class Distribution {
    public abstract double get(double percentile);
    public abstract boolean isInBounds(double value);
}
