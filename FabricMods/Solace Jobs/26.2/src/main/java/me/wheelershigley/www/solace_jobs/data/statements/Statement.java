package me.wheelershigley.www.solace_jobs.data.statements;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statement {
    private final HashMap<Identifier, Double> incomes = new HashMap<>();
    private final Instant timeSinceLastPoll;

    public Statement() {
        timeSinceLastPoll = Instant.now(
            Clock.systemDefaultZone()
        );
    }

    public void add(Identifier source, double amount) {
        double previous_amount = incomes.getOrDefault(source, 0.0);
        incomes.put(source, previous_amount + amount);
    }

    public long getSecondsElapsed() {
        return Instant.now().getEpochSecond() - timeSinceLastPoll.getEpochSecond();
    }

    public HashMap<Identifier, Double> getHourly() {
        double hours = ( (double)getSecondsElapsed() ) / 3600.0;

        HashMap<Identifier, Double> hourly = new HashMap<>();
        for( Map.Entry<Identifier, Double> income : incomes.entrySet() ) {
            hourly.put(income.getKey(), income.getValue() / hours);
        }

        return hourly;
    }

    public void reset() {
        incomes.clear();
    }

    public List<Component> popAsTextComponents() {
        HashMap<Identifier, Double> incomesCopy = new HashMap<>(incomes);
        double sum = 0.0;
        for( Map.Entry<Identifier, Double> income : incomesCopy.entrySet() ) {
            sum += income.getValue();
        }

        List<Component> incomeStatement; {
            incomeStatement = new ArrayList<>();
            incomeStatement.add(
                Component.translatable(
                    "solace_jobs.income_statement.header",
                    this.getSecondsElapsed() / 60.0
                )
            );
            incomeStatement.add(
                Component.translatable("solace_jobs.income_statement.seperator")
            );
            for( Map.Entry<Identifier, Double> income : incomesCopy.entrySet() ) {
                incomeStatement.add(
                    Component.translatable(
                        "solace_jobs.income_statement.line",
                        Component.translatable(
                            income.getKey().getNamespace() + ".job." + income.getKey().getPath()
                        ),
                        income.getValue()
                    )
                );
            }
            incomeStatement.add(
                Component.translatable("solace_jobs.income_statement.seperator")
            );
            incomeStatement.add(
                Component.translatable(
                    "solace_jobs.income_statement.total",
                    sum
                )
            );
            incomeStatement.add(
                Component.translatable("solace_jobs.income_statement.seperator")
            );
        }

        reset();
        return incomeStatement;
    }
    public HashMap<Identifier, Double> pop() {
        HashMap<Identifier, Double> incomesCopy = new HashMap<>(incomes);
        reset();
        return incomesCopy;
    }
    public HashMap<Identifier, Double> popHourly() {
        HashMap<Identifier, Double> hourly = getHourly();
        reset();
        return hourly;
    }
}
