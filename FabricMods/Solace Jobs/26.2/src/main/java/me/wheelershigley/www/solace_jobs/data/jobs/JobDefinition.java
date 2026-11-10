package me.wheelershigley.www.solace_jobs.data.jobs;

import me.wheelershigley.www.solace_jobs.IdentifierHelper;
import me.wheelershigley.www.solace_jobs.rewards.ActionReward;
import me.wheelershigley.www.solace_jobs.rewards.ActionType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class JobDefinition {
    public final Identifier identifier;
    public Identifier IN_ITEM_IDENTIFIER  = IdentifierHelper.identifierOf( Items.STAINED_GLASS_PANE.green() );
    public Identifier OUT_ITEM_IDENTIFIER = IdentifierHelper.identifierOf( Items.STAINED_GLASS_PANE.red()   );
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
    public JobDefinition(
        Identifier identifier,
        Identifier in_item_identifier, Identifier out_item_identifier,
        Set<IdentifiableReward> rewarded_actions
    ) {
        this.identifier = identifier;
        this.IN_ITEM_IDENTIFIER = in_item_identifier;
        this.OUT_ITEM_IDENTIFIER = out_item_identifier;
        this.rewarded_actions.addAll(rewarded_actions);
    }

    public String getTranslationKey() {
        return this.identifier.getNamespace() + ".job." + this.identifier.getPath();
    }

    public boolean addReward(IdentifiableReward identified_reward) {
        for(IdentifiableReward rewarded_action : rewarded_actions) {
            if( rewarded_action.action().contains(
                identified_reward.action().IDENTIFIER,
                identified_reward.action().actionType
            ) ) {
                return false;
            }
        }

        rewarded_actions.add(identified_reward);
        return true;
    }
    public void removeReward(Identifier sourceIdentifier) {
        rewarded_actions.removeIf(
            (rewarded_action) -> {
                return rewarded_action.action().contains(
                    sourceIdentifier,
                    rewarded_action.action().actionType
                );
            }
        );
    }

    public @Nullable ActionReward getFirstReward(Identifier potentialSource, ActionType actionType) {
        for(IdentifiableReward rewarded_action : rewarded_actions) {
            if(rewarded_action.action().contains(potentialSource, actionType) ) {
                return rewarded_action.reward();
            }
        }
        return null;
    }

    public Item getInItem() {
        return IdentifierHelper.itemOf(IN_ITEM_IDENTIFIER);
    }
    public Item getOutItem() {
        return IdentifierHelper.itemOf(OUT_ITEM_IDENTIFIER);
    }

    @Override
    public String toString() {
        return identifier.toString();
    }
}
