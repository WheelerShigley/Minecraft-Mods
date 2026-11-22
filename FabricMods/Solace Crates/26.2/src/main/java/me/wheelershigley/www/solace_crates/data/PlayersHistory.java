package me.wheelershigley.www.solace_crates.data;

import me.wheelershigley.www.solace_crates.api.data.Savable;

import java.util.HashMap;
import java.util.UUID;

public class PlayersHistory implements Savable< HashMap<UUID, CrateHistory> > {
    private HashMap<UUID, CrateHistory> playerHistories = new HashMap<>();

    public CrateHistory get(UUID playerIdentifier) {
        if( !playerHistories.containsKey(playerIdentifier) ) {
            playerHistories.put(
                playerIdentifier,
                new CrateHistory()
            );
        }

        return playerHistories.get(playerIdentifier);
    }

    @Override
    public HashMap<UUID, CrateHistory> getData() {
        return playerHistories;
    }

    @Override
    public void setData(HashMap<UUID, CrateHistory> data) {
        this.playerHistories = data;
    }
}
