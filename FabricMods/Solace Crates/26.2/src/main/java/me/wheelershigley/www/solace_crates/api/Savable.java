package me.wheelershigley.www.solace_crates.api;

public interface Savable<T> {
    T getData();
    void setData(T data);
}
