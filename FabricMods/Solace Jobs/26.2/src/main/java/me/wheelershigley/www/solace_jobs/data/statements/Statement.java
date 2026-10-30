package me.wheelershigley.www.solace_jobs.data.statements;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
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

            double total_seconds = this.getSecondsElapsed();
            int _minutes = (int)Math.floor( total_seconds/60.0 );
            int _seconds = (int)(
                total_seconds - 60.0 * ( (double)_minutes )
            );
            String _secondsAsString = Integer.toString(_seconds);
            if(_secondsAsString.length() < 2) {
                _secondsAsString = "0" + _secondsAsString;
            }

            incomeStatement.add(
                Component.translatable(
                    "solace_jobs.income_statement.header",
                    _minutes, _secondsAsString
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
                        ).setStyle(
                            Style.EMPTY.withColor(TextColor.GRAY)
                        ),
                        Component.translatable(
                            "solace_jobs.currency.display",
                            Double.toString(  Math.round( 100.0*income.getValue() )/100.0  )
                        ).setStyle(
                            Style.EMPTY.withColor(TextColor.GREEN)
                        )
                    )
                );
            }
            incomeStatement.add(
                Component.translatable("solace_jobs.income_statement.seperator")
            );
            if(1 < incomesCopy.size() ) {
                incomeStatement.add(
                    Component.translatable(
                        "solace_jobs.income_statement.total",
                        Component.translatable(
                            "solace_jobs.currency.display",
                            Double.toString( Math.round(100.0*sum) / 100.0 )
                        ).setStyle(
                            Style.EMPTY.withColor(TextColor.GREEN)
                        )
                    )
                );
                incomeStatement.add(
                    Component.translatable("solace_jobs.income_statement.seperator")
                );
            }
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
