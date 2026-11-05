package me.wheelershigley.www.solace_jobs.data.jobs;

import me.wheelershigley.www.solace_jobs.data.Savable;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JobDefinitions implements Savable< Set<JobDefinition> > {
    private Set<JobDefinition> jobs = new HashSet<>();

    public JobDefinitions() {}
    public JobDefinitions(Set<JobDefinition> jobs) {
        this.jobs = jobs;
    }

    public void addType(JobDefinition definition) {
        jobs.add(definition);
    }
    public void removeType(Identifier jobIdentifier) {
        for(JobDefinition job : jobs) {
            if( job.identifier.equals(jobIdentifier) ) {
                jobs.remove(job);
                return;
            }
        }
    }

    public @Nullable JobDefinition getJob(Identifier jobIdentifier) {
        Iterator<JobDefinition> jobsIterator = jobs.iterator();

        JobDefinition job = null;
        while( jobsIterator.hasNext() ) {
            job = jobsIterator.next();
            if( job.identifier.equals(jobIdentifier) ) {
                return job;
            }
        }
        return null;
    }
    public Set<JobDefinition> getJobs() {
        return this.jobs;
    }

    public Set<JobDefinition> getJobsFor(Identifier target) {
        Set<JobDefinition> jobs = new HashSet<>();
        for(JobDefinition job : this.jobs) {
            for(IdentifiableReward reward : job.rewarded_actions) {
                if( reward.action().contains(target, reward.action().actionType) ) {
                    jobs.add(job);
                }
            }
        }
        return jobs;
    }

    @Override
    public Set<JobDefinition> getData() {
        return jobs;
    }

    @Override
    public void setData(Set<JobDefinition> data) {
        jobs.clear();
        jobs.addAll(data);
    }
}
