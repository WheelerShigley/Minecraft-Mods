package me.wheelershigley.www.solace_jobs.data.players;

import net.minecraft.resources.Identifier;

import java.util.HashMap;

public record JobsPlayerData(
    HashMap<Identifier, Double> experiences
) {}
