package me.wheelershigley.www.solace_crates.data;

import me.wheelershigley.www.solace_crates.api.data.Savable;
import me.wheelershigley.www.solace_crates.registry.CrateRegistry;

import java.util.LinkedHashSet;
import java.util.Set;

public class CrateDefinitions implements Savable< Set<CrateDefinition> > {
    private Set<CrateDefinition> definitions = new LinkedHashSet<>();

    public CrateDefinitions() {}
    public CrateDefinitions(CrateDefinition definition) {
        addDefinition(definition);
    }
    public CrateDefinitions(Set<CrateDefinition> definitions) {
        definitions.forEach(this::addDefinition);
    }

    public boolean addDefinition(CrateDefinition definition) {
        boolean wasAdded = definitions.add(definition);
        if(
            !CrateRegistry.contains( definition.IDENTIFIER() )
        ) {
            CrateRegistry.register(definition);
        }
        return wasAdded;
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
