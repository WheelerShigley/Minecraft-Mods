package me.wheelershigley.www.solace_jobs.data.jobs;

import me.wheelershigley.www.solace_jobs.data.ActionReward;
import net.minecraft.resources.Identifier;

public record IdentifiableReward(
    IdentifiableSource<Identifier> source,
    ActionReward reward
) {}
