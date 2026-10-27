package me.wheelershigley.www.solace_jobs.data.jobs;

import me.wheelershigley.www.solace_jobs.data.ActionType;
import me.wheelershigley.www.solace_jobs.data.Savable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JobDefinitions implements Savable< Map<ActionType, JobDefinition> > {
    private Map<ActionType, JobDefinition> jobs = new HashMap<>();

    public JobDefinitions() {}
    public JobDefinitions(HashMap<ActionType, JobDefinition> jobs) {
        this.jobs = jobs;
    }

    public void addType(ActionType type, JobDefinition definition) {
        jobs.put(type, definition);
    }
    public void removeType(ActionType type) {
        jobs.remove(type);
    }

    public Set<JobDefinition> getJobs(ActionType actionType) {
        Set<JobDefinition> jobsWithActionType = new HashSet<>();
        for( Map.Entry<ActionType, JobDefinition> entry : jobs.entrySet() ) {
            jobsWithActionType.add( entry.getValue() );
        }
        return jobsWithActionType;
    }

    @Override
    public Map<ActionType, JobDefinition> getData() {
        return jobs;
    }

    @Override
    public void setData(Map<ActionType, JobDefinition> data) {
        jobs.clear();
        jobs.putAll(data);
    }
}
