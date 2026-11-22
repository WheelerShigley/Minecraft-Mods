package me.wheelershigley.www.solace_crates.data;

import net.minecraft.resources.Identifier;

import java.util.HashMap;

public class CrateHistory {
    private final HashMap<Identifier, Integer> history = new HashMap<>();

    public void increment(Identifier identifier) {
        int count = history.getOrDefault(identifier, 0);
        history.put(identifier, count + 1);
    }

    public int get(Identifier identifier) {
        return history.getOrDefault(identifier, 0);
    }
}
