package me.wheelershigley.www.solace_jobs.data.jobs;

import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.Set;

public class IdentifiableSource<T> {
    public final Identifier identifier;
    public final SourceType type;
    Set<T> sources;

    public IdentifiableSource(
        Identifier identifier,
        SourceType type
    ) {
        this.identifier = identifier;
        this.type = type;
        this.sources = new HashSet<>();
    }
    public IdentifiableSource(
        Identifier identifier,
        SourceType type,
        Set<T> sources
    ) {
        this.identifier = identifier;
        this.type = type;
        this.sources = sources;
    }

    public boolean contains(T potentialSource) {
        return sources.contains(potentialSource);
    }

    public boolean addSource(T source) {
        if( this.sources.contains(source) ) {
            return false;
        }
        sources.add(source);
        return true;
    }
    public void addSources(Set<T> sources) {
        this.sources.addAll(sources);
    }

    public void removeSource(T source) {
        this.sources.remove(source);
    }
}
