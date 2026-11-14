package me.wheelershigley.www.solace_crates.data;

import me.wheelershigley.www.solace_crates.registry.CrateRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrateDefinition {
    public final Identifier IDENTIFIER;
    public final Identifier MODEL;
    public final Identifier LOOTTABLE;

    public final Identifier STANDARD_KEY;
    public final Identifier PREMIUM_KEY;
    public final int PITY_THRESHOLD;

    public CrateDefinition(
        @NotNull Identifier identifier,
        @NotNull  Identifier modelIdentifier,
        @NotNull  Identifier lootTable,
        @Nullable Identifier standardKeyIdentifier,
        @Nullable Identifier premiumKeyIdentifier,
                  int pity_threshold
    ) {
        IDENTIFIER     = identifier;
        MODEL          = modelIdentifier;
        LOOTTABLE      = lootTable;
        STANDARD_KEY   = standardKeyIdentifier;
        PREMIUM_KEY    = premiumKeyIdentifier;
        PITY_THRESHOLD = pity_threshold;

        CrateRegistry.register(this);
    }

    public @Nullable LootTable getLootTable(MinecraftServer server) {
        return server
            .reloadableRegistries()
            .getLootTable(
                ResourceKey.create(Registries.LOOT_TABLE, this.LOOTTABLE)
            )
        ;
    }
}
