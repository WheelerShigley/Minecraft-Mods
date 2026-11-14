package me.wheelershigley.www.solace_crates.api;

import me.wheelershigley.www.solace_crates.SolaceCrates;
import me.wheelershigley.www.solace_crates.configurations.CrateLootTablesLocation;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootTable;

import java.io.IOError;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public interface ResourceLocation<T> {
    void initialize(MinecraftServer server);
    void reload(MinecraftServer server);

    String getLocationName();
    T getMappedData(
        final MinecraftServer server,
        final Path path, final String name
    );
    com.mojang.serialization.Codec<T> getCodec();

    default Path getDataPath() {
        return FabricLoader.getInstance()
            .getConfigDir()
            .resolve(SolaceCrates.MOD_ID)
            .resolve( getLocationName() )
        ;
    }

    default Map<String, T> read(
        final MinecraftServer server,
        final com.mojang.serialization.Codec<T> codec
    ) throws IOException {
        Path root = getDataPath();
        Files.createDirectories(root);

        AtomicReference< Map<String, T> > result = new AtomicReference<>( new HashMap<>() );
        try( Stream<Path> files = Files.walk(root) ) {
            files
                .filter(Files::isRegularFile)
                .filter(
                    path -> path.toString().endsWith(".json")
                )
                .forEach(
                    (path) -> {
                        String name = root.relativize(path)
                            .toString()
                            .replace('\\', '/')
                            .replaceFirst("\\.json$", "")
                        ;
                        T current = getMappedData(server, path, name);
                        if(current != null) {
                            result.get().put(name, current);
                        }
                    }
                )
            ;
        } catch(UncheckedIOException ioException) {
            throw ioException.getCause();
        }

        return result.get();
    }
}