package me.wheelershigley.www.solace_crates.configurations;

import com.google.common.reflect.TypeToken;
import me.wheelershigley.www.solace_crates.api.data.Codec;
import me.wheelershigley.www.solace_crates.data.CrateHistory;
import me.wheelershigley.www.solace_crates.data.PlayersHistory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

public class PlayersHistoryCodec implements Codec< PlayersHistory, HashMap<UUID, CrateHistory> >  {
    public static final PlayersHistoryCodec INSTANCE = new PlayersHistoryCodec();
    private static PlayersHistory DATA = INSTANCE.load();

    public static void reload() {
        DATA = INSTANCE.load();
    }

    @Override
    public PlayersHistory getDefaultCodec() {
        return new PlayersHistory();
    }

    @Override
    public PlayersHistory getEmptyData() {
        return new PlayersHistory();
    }

    @Override
    public PlayersHistory getInstanceData() {
        return DATA;
    }

    @Override
    public Type getDataType() {
        return new TypeToken< HashMap<UUID, CrateHistory> >() {}.getType();
    }

    @Override
    public String getConfigName() {
        return "player_data.json";
    }
}
