package me.wheelershigley.www.solace_crates.api.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.wheelershigley.www.solace_crates.SolaceCrates;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Codec< T extends Savable<D>, D > {
    T getDefaultCodec();
    T getEmptyData();
    T getInstanceData();
    Type getDataType();

    String getConfigName();
    default Path getDataPath() {
        return FabricLoader.getInstance()
            .getConfigDir()
            .resolve(SolaceCrates.MOD_ID)
            .resolve( getConfigName() )
            .toAbsolutePath()
            .normalize()
        ;
    }

    default Gson getGson() {
        return new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapterFactory(
                new OptionalTypeAdapterFactory()
            )
            .registerTypeAdapter(
                Identifier.class,
                new IdentifierTypeAdapter()
            )
            .create()
        ;
    }

    default T load() {
        Path path = getDataPath();
        if( !Files.exists(path) ) {
            return getDefaultCodec();
        }

        try( Reader reader = Files.newBufferedReader(path) ) {
            String json = reader.readAllAsString();
            D data = getGson().fromJson(
                json,
                getDataType()
            );
            if(data == null) {
                return getDefaultCodec();
            }

            SolaceCrates.LOGGER.info( "Successfully loaded \"{}\"", path.toAbsolutePath() );
            T newData = getInstanceData();
            if(newData == null) {
                newData = getDefaultCodec();
            }
            newData.setData(data);
            return newData;
        } catch(IOException exception) {
            throw new RuntimeException(
                "Failed to load " + getConfigName(), exception
            );
        }
    }
    default boolean save() {
        Path path = getDataPath();

        final T DATA_SOURCE = getInstanceData();
        if(DATA_SOURCE == null) {
            SolaceCrates.LOGGER.warn("No instance data.");
            return false;
        }

        final D DATA = DATA_SOURCE.getData();
        if(DATA == null) {
            SolaceCrates.LOGGER.warn("No data available.");
            return false;
        }

        try {
            Files.createDirectories( path.getParent() );

            try( BufferedWriter writer = Files.newBufferedWriter(path) ) {
                getGson().toJson(DATA, getDataType(), writer);
            }
            SolaceCrates.LOGGER.info("Saved configuration: {}", path);
            return true;
        } catch(IOException exception) {
            throw new RuntimeException(
                "Failed to save " + getConfigName(), exception
            );
        }
    }
}
