package me.wheelershigley.www.solace_crates.api;

import java.util.List;

public abstract class OddsTables<O extends OddsTable< List<T> >, T> {
    private final List<O> tables;

    public OddsTables(O table) {
        this.tables = List.of(table);
    }
    public OddsTables(List<O> oddsTables) {
        this.tables = oddsTables;
    }

    public List<O> getOddsTables() {
        return tables;
    }

    public abstract List<T> getAllEntries();

    public int getTotalEntriesCount() {
        int accumulator = 0;
        for(OddsTable< List<T> > table : tables) {
            accumulator += table.getData().size();
        }
        return accumulator;
    }
}
