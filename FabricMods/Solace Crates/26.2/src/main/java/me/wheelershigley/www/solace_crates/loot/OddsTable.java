package me.wheelershigley.www.solace_crates.loot;

import java.util.*;
import java.util.stream.Collectors;

public interface OddsTable<T> {
    HashMap<T, Double> getData();

    default void addOdd(final T key, final double value) {
        getData().put(key, value);
    }
    default void setOdds(Map<T, Double> data) {
        getData().clear();
        getData().putAll(data);
    }

    default Map<T, Double> getSorted() {
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
    default void sort() {
        Map<T, Double> sorted = getSorted();
        getData().clear();
        getData().putAll(sorted);
    }
    default void normalize() {
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
