package me.wheelershigley.www.solace_crates.data;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrateDefinition {
    public final Identifier MODEL;
    public final LootTable LOOTTABLE;

    public final Identifier STANDARD_KEY;
    public final Identifier PREMIUM_KEY;
    public final int PITY_THRESHOLD;

    public CrateDefinition(
        @NotNull  Identifier modelIdentifier,
        @NotNull  LootTable lootTable,
        @Nullable Identifier standardKeyIdentifier,
        @Nullable Identifier premiumKeyIdentifier,
                  int pity_threshold
    ) {
        MODEL          = modelIdentifier;
        LOOTTABLE      = lootTable;
        STANDARD_KEY   = standardKeyIdentifier;
        PREMIUM_KEY    = premiumKeyIdentifier;
        PITY_THRESHOLD = pity_threshold;
    }
}
