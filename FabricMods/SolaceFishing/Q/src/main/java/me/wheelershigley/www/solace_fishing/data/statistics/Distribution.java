package me.wheelershigley.www.solace_fishing.data.statistics;

public abstract class Distribution {
    public abstract double get(double percentile);
    public abstract boolean isInBounds(double value);
}
