package me.wheelershigley.www.solace_jobs.data.config;

import me.wheelershigley.www.solace_jobs.data.Savable;

public class Configurations implements Savable<Configuration>  {
    private Configuration data;

    public Configurations(Configuration data) {
        this.data = data;
    }

    @Override
    public Configuration getData() {
        return data;
    }

    @Override
    public void setData(Configuration data) {
        this.data = data;
    }
}
