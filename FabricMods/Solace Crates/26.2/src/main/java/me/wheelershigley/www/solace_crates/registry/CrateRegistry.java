package me.wheelershigley.www.solace_crates.registry;

import me.wheelershigley.www.solace_crates.data.CrateDefinition;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class CrateRegistry {
    private static final Map<Identifier, CrateDefinition> CRATES = new HashMap<>();

    public static void initialize() {}

    public static void register(@NotNull CrateDefinition crate) {
        if( CRATES.containsKey(crate.IDENTIFIER()) ) {
            throw new IllegalArgumentException(
                "Crate, \"" + crate.IDENTIFIER() + "\", already registered."
            );
        }

        CRATES.put(crate.IDENTIFIER(), crate);
    }

    public static @Nullable CrateDefinition get(@NotNull Identifier identifier) {
        return CRATES.get(identifier);
    }

    public static boolean contains(@NotNull Identifier identifier) {
        return CRATES.containsKey(identifier);
    }

    public static boolean unregister(@NotNull Identifier identifier) {
        return CRATES.remove(identifier) != null;
    }

    public static void clear() {
        CRATES.clear();
    }

    public static @NotNull Collection<CrateDefinition> values() {
        return CRATES.values();
    }
}