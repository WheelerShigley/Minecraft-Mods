package me.wheelershigley.www.solace_jobs.data.jobs;

import com.google.common.reflect.TypeToken;
import me.wheelershigley.www.solace_jobs.SolaceJobs;
import me.wheelershigley.www.solace_jobs.data.ActionReward;
import me.wheelershigley.www.solace_jobs.data.ActionType;
import me.wheelershigley.www.solace_jobs.data.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static me.wheelershigley.www.solace_jobs.SolaceJobs.solaceJobsIdentifier;

public class JobsCodec implements Codec< JobDefinitions, Map<ActionType, JobDefinition> > {
    public static final JobsCodec INSTANCE = new JobsCodec();
    public static final JobDefinitions DATA = INSTANCE.load();

    @Override
    public JobDefinitions getEmptyData() {
        return new JobDefinitions();
    }

    public JobDefinitions getInstanceData() {
        return DATA;
    }

    @Override
    public Type getDataType() {
        return new TypeToken< Map<ActionType, JobDefinition> >() {}.getType();
    }

    @Override
    public String getConfigName() {
        return "solace_jobs_definitions.json";
    }

    @Override
    public JobDefinitions getDefaultCodec() {
        JobDefinitions jobs = new JobDefinitions();

        /* Excavator */
        JobDefinition excavator = new JobDefinition(
            solaceJobsIdentifier("excavator")
        );
        excavator.addReward(
            getIdentifiedReward(
                "grasses",
                Set.of(Blocks.GRASS_BLOCK, Blocks.MYCELIUM, Blocks.PODZOL),
                0.5, 1, 0.002
            )
        );
        jobs.addType(ActionType.BREAK, excavator);

        /* Next */

        return jobs;
    }

    private IdentifiableReward getIdentifiedReward(
        String name,
        Set<Block> blocks,
        double experience, double job_experience, double currency
    ) {
        return new IdentifiableReward(
            new IdentifiableSource<>(
                SolaceJobs.solaceJobsIdentifier(name),
                SourceType.Singles,
                getBlockIdentifiers(blocks)
            ),
            new ActionReward(experience, job_experience, currency)
        );
    }
    private Set<Identifier> getBlockIdentifiers(Set<Block> blocks) {
        Set<Identifier> result = new HashSet<>();
        for(Block block : blocks) {
            result.add(
                BuiltInRegistries.BLOCK.getKey(block)
            );
        }
        return result;
    }
}
