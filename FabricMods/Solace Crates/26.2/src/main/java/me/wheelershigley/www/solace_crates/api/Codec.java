package me.wheelershigley.www.solace_crates.api;

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
        Path   configDir = FabricLoader.getInstance().getConfigDir();
               configDir = configDir.resolve(SolaceCrates.MOD_ID).resolve( getConfigName() );
        return configDir;
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
            D data = getGson().fromJson(
                reader,
                getDataType()
            );
            if(data == null) {
                return getDefaultCodec();
            }

            T newData = getEmptyData();
            newData.setData(data);
            SolaceCrates.LOGGER.info( "Successfully loaded \"{}\"", path.toAbsolutePath() );
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

            try( BufferedWriter writer = Files.newBufferedWriter(path) ) {
                final T DATA_SOURCE = getInstanceData();
                if(DATA_SOURCE == null) {
                    SolaceCrates.LOGGER.warn( "Failed to load from \"{}\".", path.toAbsolutePath() );
                    return false;
                }
                final D DATA = DATA_SOURCE.getData();
                if(DATA == null) {
                    SolaceCrates.LOGGER.warn( "Failed to save from \"{}\".", path.toAbsolutePath() );
                    return false;
                }

                getGson().toJson(
                    DATA,
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
