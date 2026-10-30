package me.wheelershigley.www.solace_jobs.data.jobs;

import me.wheelershigley.www.solace_jobs.rewards.ActionReward;
import net.minecraft.resources.Identifier;

public record IdentifiableReward(
    IdentifiableSource<Identifier> action,
    ActionReward reward
) {}
