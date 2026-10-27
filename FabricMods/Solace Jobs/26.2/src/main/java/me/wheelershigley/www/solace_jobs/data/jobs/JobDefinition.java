package me.wheelershigley.www.solace_jobs.data.jobs;

import me.wheelershigley.www.solace_jobs.data.ActionReward;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class JobDefinition {
    public final Identifier identifier;
    public final Set<IdentifiableReward> rewarded_actions = new HashSet<>();

    public JobDefinition(Identifier identifier) {
        this.identifier = identifier;
    }
    public JobDefinition(
        Identifier identifier,
        Set<IdentifiableReward> rewarded_actions
    ) {
        this.identifier = identifier;
        this.rewarded_actions.addAll(rewarded_actions);
    }

    public boolean addReward(IdentifiableReward identified_reward) {
        for(IdentifiableReward rewarded_action : rewarded_actions) {
            if( rewarded_action.action().contains(identified_reward.action().IDENTIFIER) ) {
                return false;
            }
        }

        rewarded_actions.add(identified_reward);
        return true;
    }
    public void removeReward(Identifier sourceIdentifier) {
        rewarded_actions.removeIf(
            (rewarded_action) -> {
                return rewarded_action.action().contains(sourceIdentifier);
            }
        );
    }

    public @Nullable ActionReward getFirstReward(Identifier potentialSource) {
        for(IdentifiableReward rewarded_action : rewarded_actions) {
            if( rewarded_action.action().contains(potentialSource) ) {
                return rewarded_action.reward();
            }
        }
        return null;
    }
}
