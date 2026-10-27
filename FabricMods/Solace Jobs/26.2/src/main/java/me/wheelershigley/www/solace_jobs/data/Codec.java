package me.wheelershigley.www.solace_jobs.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        return FabricLoader
            .getInstance()
            .getConfigDir()
            .resolve( getConfigName() )
        ;
    }

    default Gson getGson() {
        return new GsonBuilder()
            .setPrettyPrinting()
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
            D data = getGson().fromJson(
                reader,
                getDataType()
            );

            T newData = getEmptyData();
            if(data != null) {
                newData.setData(data);
            }
            return newData;
        } catch(IOException exception) {
            throw new RuntimeException(
                "Failed to load " + getConfigName(), exception
            );
        }
    }
    default boolean save() {
        Path path = getDataPath();

        try {
            Files.createDirectories( path.getParent() );

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                getGson().toJson(
                    getInstanceData().getData(),
                    getDataType(),
                    writer
                );
            }

            return true;
        } catch(IOException exception) {
            throw new RuntimeException(
                "Failed to save " + getConfigName(), exception
            );
        }
    }
}
