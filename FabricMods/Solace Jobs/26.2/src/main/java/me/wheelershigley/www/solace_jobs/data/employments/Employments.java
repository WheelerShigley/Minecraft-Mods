package me.wheelershigley.www.solace_jobs.data.employments;

import me.wheelershigley.www.solace_jobs.data.Savable;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Employments implements Savable< HashMap< UUID, Set<Identifier> > > {
    public HashMap< UUID, Set<Identifier> > employments = new HashMap<>();

    public Employments() {}
    public Employments(HashMap< UUID, Set<Identifier> > employments) {
        this.employments = employments;
    }

    public boolean join(UUID playerIdentifier, Identifier employmentIdentifier) {
        Set<Identifier> employments = this.employments.getOrDefault(
            playerIdentifier,
            new HashSet<>()
        );
        //TODO: check join limits and react
        employments.add(employmentIdentifier);
        this.employments.put(playerIdentifier, employments);
        return true;
    }

    public void leave(UUID playerIdentifier, Identifier employmentIdentifier) {
        Set<Identifier> employments = this.employments.getOrDefault(
            playerIdentifier,
            new HashSet<>()
        );
        employments.remove(employmentIdentifier);
        this.employments.put(playerIdentifier, employments);
    }

    @Override
    public HashMap< UUID, Set<Identifier> > getData() {
        return this.employments;
    }

    @Override
    public void setData(HashMap< UUID, Set<Identifier> > data) {
        this.employments = data;
    }
}
