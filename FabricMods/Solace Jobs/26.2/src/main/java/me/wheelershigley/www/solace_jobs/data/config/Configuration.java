package me.wheelershigley.www.solace_jobs.data.config;

public record Configuration(
    String economyProviderName,
    String economyCurrencyName,
    double global_experience_multiplier,
    double global_job_experience_multiplier,
    double global_earnings_multiplier,
    double level_exponent
) {}
