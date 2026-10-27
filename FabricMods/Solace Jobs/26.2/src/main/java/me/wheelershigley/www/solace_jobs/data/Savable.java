package me.wheelershigley.www.solace_jobs.data;

public interface Savable<T> {
    T getData();
    void setData(T data);
}
