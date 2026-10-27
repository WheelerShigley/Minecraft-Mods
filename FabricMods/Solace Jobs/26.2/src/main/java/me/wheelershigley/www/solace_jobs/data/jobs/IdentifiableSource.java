package me.wheelershigley.www.solace_jobs.data.jobs;

import me.wheelershigley.www.solace_jobs.data.ActionType;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.Set;

public class IdentifiableSource<T> {
    public final Identifier IDENTIFIER;
    public SourceType sourceType = SourceType.Singles;
    public ActionType actionType = ActionType.BREAK;
    public Set<T> sources = new HashSet<>();

    public IdentifiableSource(
        Identifier identifier,
        Set<T> sources
    ) {
        this.IDENTIFIER = identifier;
        this.sources = sources;
    }
    public IdentifiableSource(
        Identifier identifier,
        ActionType actionType,
        Set<T> sources
    ) {
        this.IDENTIFIER = identifier;
        this.actionType = actionType;
        this.sources = sources;
    }
    public IdentifiableSource(
        Identifier identifier,
        SourceType sourceType,
        Set<T> sources
    ) {
        this.IDENTIFIER = identifier;
        this.sourceType = sourceType;
        this.sources = sources;
    }
    public IdentifiableSource(
        Identifier identifier,
        ActionType actionType,
        SourceType sourceType,
        Set<T> sources
    ) {
        this.IDENTIFIER = identifier;
        this.actionType = actionType;
        this.sourceType = sourceType;
        this.sources = sources;
    }

    public boolean contains(T potentialSource, ActionType actionType) {
        if( this.actionType != actionType ) {
            return false;
        }
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
