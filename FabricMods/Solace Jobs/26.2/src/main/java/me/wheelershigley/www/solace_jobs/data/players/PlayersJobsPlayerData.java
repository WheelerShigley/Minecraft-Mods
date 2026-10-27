package me.wheelershigley.www.solace_jobs.data.players;

import me.wheelershigley.www.solace_jobs.data.Savable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class PlayersJobsPlayerData implements Savable< HashMap<UUID, JobsPlayerData> > {
    private final HashMap<UUID, JobsPlayerData> DATA = new HashMap<>();

    public @Nullable JobsPlayerData get(UUID playerIdentifier) {
        return DATA.get(playerIdentifier);
    }

    public void set(HashMap<UUID, JobsPlayerData> data) {
        DATA.clear();
        DATA.putAll(data);
    }
    public void set(UUID playerIdentifier, JobsPlayerData playerData) {
        DATA.put(playerIdentifier, playerData);
    }

    @Override
    public HashMap<UUID, JobsPlayerData> getData() {
        return DATA;
    }

    @Override
    public void setData(HashMap<UUID, JobsPlayerData> data) {
        set(data);
    }
}
