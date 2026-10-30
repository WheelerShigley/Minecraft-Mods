package me.wheelershigley.www.solace_jobs.data.config;

import com.google.common.reflect.TypeToken;
import me.wheelershigley.www.solace_jobs.data.Codec;

import java.lang.reflect.Type;

public class ConfigurationsCodec implements Codec<Configurations, Configuration> {
    public static final ConfigurationsCodec INSTANCE = new ConfigurationsCodec();
    private static Configurations DATA = INSTANCE.load();

    public static void reload() {
        DATA = INSTANCE.load();
    }

    @Override
    public Configurations getDefaultCodec() {
        return new Configurations(
            new Configuration(
                "savs_common_economy",
                "dollar",
                1.0,
                1.0,
                1.0,
                Math.sqrt(2.0)
            )
        );
    }

    @Override
    public Configurations getEmptyData() {
        return getDefaultCodec();
    }

    @Override
    public Configurations getInstanceData() {
        return DATA;
    }

    @Override
    public Type getDataType() {
        return new TypeToken<Configuration>() {}.getType();
    }

    @Override
    public String getConfigName() {
        return "configuration.json";
    }
}
