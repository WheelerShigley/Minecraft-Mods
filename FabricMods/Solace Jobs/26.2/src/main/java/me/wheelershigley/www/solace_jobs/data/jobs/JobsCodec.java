package me.wheelershigley.www.solace_jobs.data.jobs;

import com.google.common.reflect.TypeToken;
import me.wheelershigley.www.solace_jobs.data.Codec;

import java.lang.reflect.Type;
import java.util.Set;

public class JobsCodec implements Codec< JobDefinitions, Set<JobDefinition> > {
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
        return new TypeToken< Set<JobDefinition> >() {}.getType();
    }

    @Override
    public String getConfigName() {
        return "solace_jobs_definitions.json";
    }

    @Override
    public JobDefinitions getDefaultCodec() {
        JobDefinitions jobs = new JobDefinitions();

        jobs.addType(DefaultJobs.MINER);
        jobs.addType(DefaultJobs.EXCAVATOR);
        jobs.addType(DefaultJobs.LUMBERJACK);
        jobs.addType(DefaultJobs.FARMER);
        jobs.addType(DefaultJobs.FISHER);
        jobs.addType(DefaultJobs.HUNTER);

        return jobs;
    }
}
