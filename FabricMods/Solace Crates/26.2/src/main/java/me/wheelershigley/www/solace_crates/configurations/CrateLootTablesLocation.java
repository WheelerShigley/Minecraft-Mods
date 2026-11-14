package me.wheelershigley.www.solace_crates.configurations;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import me.wheelershigley.www.solace_crates.SolaceCrates;
import me.wheelershigley.www.solace_crates.api.ResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.io.IOError;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CrateLootTablesLocation implements ResourceLocation<
    Holder<LootTable>,
    Map< String, Holder<LootTable> >
> {
    public static final CrateLootTablesLocation INSTANCE = new CrateLootTablesLocation();
    private final Map< String, Holder<LootTable> > DATA = new HashMap<>();

    @Override
    public void initialize(MinecraftServer server) {
        this.reload(server);
    }

    @Override
    public String getLocationName() {
        return "loot_tables";
    }

    @Override
    public @Nullable Holder<LootTable> getMappedData(
        final MinecraftServer server,
        final Path path, final String name
    ) {
        RegistryOps<JsonElement> ops = RegistryOps.create(
            JsonOps.INSTANCE,
            server.reloadableRegistries().lookup()
        );

        AtomicReference< Holder<LootTable> > result = new AtomicReference<>();
        try( Reader reader = Files.newBufferedReader(path) ) {
            JsonElement json = JsonParser.parseReader(reader);
            getCodec()
                .parse(ops, json)
                .resultOrPartial(
                    (error) -> {
                        SolaceCrates.LOGGER.error("Failed to load \"{}\": \"{}\"", name, error);
                    }
                )
                .ifPresent(
                    (value) -> {
                        SolaceCrates.LOGGER.info("Loaded \"{}\" at \"{}\"", name, path);
                        result.set(value);
                    }
                )
            ;
        } catch(IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
        if(result.get() == null) {
            return null;
        }
        //TODO: when empty, create default (with "chests/trial_chambers/reward_ominous")
        return result.get();
    }

    @Override
    public Codec< Holder<LootTable> > getCodec() {
        return LootTable.CODEC;
    }

    @Override
    public Map< String, Holder<LootTable> > getData() {
        return this.DATA;
    }

    @Override
    public void reload(MinecraftServer server) {
        Map< String, Holder<LootTable> > map;
        try {
            map = new CrateLootTablesLocation().read( server, this.getCodec() );
        } catch(IOException ioException) {
            throw new IOError(ioException);
        }
        this.DATA.clear();
        this.DATA.putAll(map);
    }
}
