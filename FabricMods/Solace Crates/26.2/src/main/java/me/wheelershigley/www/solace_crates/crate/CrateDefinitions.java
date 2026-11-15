package me.wheelershigley.www.solace_crates.crate;

import me.wheelershigley.www.solace_crates.api.Savable;

import java.util.LinkedHashSet;
import java.util.Set;

public class CrateDefinitions implements Savable< Set<CrateDefinition> > {
    private Set<CrateDefinition> definitions = new LinkedHashSet<>();

    public CrateDefinitions() {}
    public CrateDefinitions(CrateDefinition definition) {
        definitions.add(definition);
    }
    public CrateDefinitions(Set<CrateDefinition> definitions) {
        this.definitions = definitions;
    }

    public boolean addDefinition(CrateDefinition definition) {
        return definitions.add(definition);
    }
    public boolean removeDefinition(CrateDefinition definition) {
        return definitions.remove(definition);
    }
    public Set<CrateDefinition> getDefinitions() {
        return definitions;
    }

    @Override
    public Set<CrateDefinition> getData() {
        return this.definitions;
    }

    @Override
    public void setData(Set<CrateDefinition> data) {
        this.definitions = data;
    }
}
