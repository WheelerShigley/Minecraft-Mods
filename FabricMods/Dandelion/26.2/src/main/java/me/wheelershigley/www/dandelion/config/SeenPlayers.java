package me.wheelershigley.www.dandelion.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import me.wheelershigley.www.dandelion.Dandelion;
import net.fabricmc.loader.api.FabricLoader;

//Largely based on Better-Join-Message's SeenPlayers.class
public class SeenPlayers {
    private static final Gson GSON = new Gson();
    private static final Type SET_TYPE = ( new TypeToken<HashSet<String>>() {} ).getType();

    private static final Path DATA_PATH = FabricLoader
        .getInstance()
        .getConfigDir()
        .resolve("dandelion_seen_players.json")
    ;
    private final Set<UUID> seenPlayers = new HashSet<>();

    public static SeenPlayers load() {
        SeenPlayers instance = new SeenPlayers();
        if( !Files.exists(DATA_PATH) )
            return instance;
        try {
            Reader reader = Files.newBufferedReader(DATA_PATH, StandardCharsets.UTF_8);
            try {
                Set<String> rawIds = (Set<String>)GSON.fromJson(reader, SET_TYPE);
                if(rawIds != null) {
                    for(String rawId : rawIds) {
                        instance.seenPlayers.add( UUID.fromString(rawId) );
                    }
                }
                reader.close();
            } catch(Throwable throwable_outer) {
                try {
                    reader.close();
                } catch (Throwable throwable_inner) {
                    throwable_outer.addSuppressed(throwable_inner);
                }
                throw throwable_outer;
            }
        } catch(IOException exception) {
            Dandelion.LOGGER.error("Failed to load seen-players-list.", exception);
        }
        return instance;
    }

    public void save() {
        try {
            Files.createDirectories(DATA_PATH.getParent(), (FileAttribute<?>[])new FileAttribute[0]);
            Set<String> rawIds = new HashSet<>();
            for(UUID id : this.seenPlayers) {
                rawIds.add( id.toString() );
            }
            Writer writer = Files.newBufferedWriter(DATA_PATH, StandardCharsets.UTF_8, new java.nio.file.OpenOption[0]);
            try {
                GSON.toJson(rawIds, writer);
                writer.close();
            } catch (Throwable throwable) {
                try {
                    writer.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            }
        } catch(IOException exception) {
            Dandelion.LOGGER.error("Failed to save seen players list.", exception);
        }
    }

    public boolean isFirstJoin(UUID playerId) {
        boolean isFirst = !this.seenPlayers.contains(playerId);
        if(isFirst) {
            this.seenPlayers.add(playerId);
            save();
        }
        return isFirst;
    }
}
