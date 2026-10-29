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

    public static boolean join(
        Employments data,
        UUID playerIdentifier, Identifier employmentIdentifier
    ) {
        Set<Identifier> employments = data.employments.getOrDefault(
            playerIdentifier,
            new HashSet<>()
        );
        //TODO: check join limits and react
        employments.add(employmentIdentifier);
        data.employments.put(playerIdentifier, employments);
        return true;
    }

    public static void leave(
        Employments data,
        UUID playerIdentifier, Identifier employmentIdentifier
    ) {
        Set<Identifier> employments = data.employments.getOrDefault(
            playerIdentifier,
            new HashSet<>()
        );
        employments.remove(employmentIdentifier);
        data.employments.put(playerIdentifier, employments);
    }

    public static boolean attemptToggle(
        Employments data,
        UUID playerIdentifier, Identifier employmentIdentifier
    ) {
        Set<Identifier> employments = data.employments.getOrDefault(
            playerIdentifier,
            new HashSet<>()
        );
        if( employments.contains(employmentIdentifier) ) {
            leave(data, playerIdentifier, employmentIdentifier);
            return true;
        } else {
            return join(data, playerIdentifier, employmentIdentifier);
        }
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
