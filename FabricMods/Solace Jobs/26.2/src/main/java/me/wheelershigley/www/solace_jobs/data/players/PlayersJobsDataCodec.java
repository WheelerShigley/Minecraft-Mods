package me.wheelershigley.www.solace_jobs.data.players;

import com.google.common.reflect.TypeToken;
import me.wheelershigley.www.solace_jobs.data.Codec;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

public class PlayersJobsDataCodec implements Codec< PlayersJobsPlayerData, HashMap<UUID, JobsPlayerData> > {
    public static final PlayersJobsDataCodec INSTANCE = new PlayersJobsDataCodec();
    public static final PlayersJobsPlayerData DATA = INSTANCE.load();

    @Override
    public PlayersJobsPlayerData getEmptyData() {
        return new PlayersJobsPlayerData();
    }

    public PlayersJobsPlayerData getInstanceData() {
        return DATA;
    }

    @Override
    public Type getDataType() {
        return new TypeToken< HashMap<UUID, JobsPlayerData> >() {}.getType();
    }

    @Override
    public String getConfigName() {
        return "data.json";
    }

    @Override
    public PlayersJobsPlayerData getDefaultCodec() {
        //empty by default
        return new PlayersJobsPlayerData();
    }
}
