package me.wheelershigley.www.solace_fishing.api.statistics;

import me.wheelershigley.www.solace_fishing.api.fishing.FishingContext;
import net.minecraft.util.RandomSource;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class SampleSpace<T, R> {
    private final LinkedHashSet<T> samples = new LinkedHashSet<>();

    public SampleSpace() {}
    @SafeVarargs
    public SampleSpace(T ...sample) {
        Collections.addAll(samples, sample);
    }
    public SampleSpace(Set<T> samples) {
        this.samples.addAll(samples);
    }

    public LinkedHashSet<T> getSamples() {
        return samples;
    }

    public abstract void normalize();
    public abstract R sample(RandomSource random, FishingContext context);
    public abstract SampleSpace<T, R> getSortedSubSpace(FishingContext context);
}
