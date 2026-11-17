package me.wheelershigley.www.solace_crates.api;

import java.util.*;
import java.util.stream.Collectors;

public abstract class OddsTable<T> {
    public abstract HashMap<T, Double> getData();

    public void addOdd(final T key, final double value) {
        getData().put(key, value);
    }
    public void setOdds(Map<T, Double> data) {
        getData().clear();
        getData().putAll(data);
    }

    public Map<T, Double> getSorted() {
        return getData().entrySet().stream()
            .sorted( Map.Entry.<T, Double>comparingByValue().reversed() )
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (a, b) -> a, LinkedHashMap::new
                )
            )
        ;
    }
    public void sort() {
        Map<T, Double> sorted = getSorted();
        getData().clear();
        getData().putAll(sorted);
    }
    public void normalize() {
        double sum_weight = getData().values().stream()
            .mapToDouble(Double::doubleValue)
            .sum()
        ;

        Map<T, Double> normalizedMap = new LinkedHashMap<>();
        for( Map.Entry<T, Double> entry : getData().entrySet() ) {
            normalizedMap.put(
                entry.getKey(),
                entry.getValue() / sum_weight
            );
        }
        setOdds(normalizedMap);
    }
}
