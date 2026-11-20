package me.wheelershigley.www.solace_crates.crate;

import me.wheelershigley.www.solace_crates.configurations.CrateLootTablesLocation;
import me.wheelershigley.www.solace_crates.registry.CrateRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record CrateDefinition(
    Identifier IDENTIFIER,
    Identifier MODEL,
    Identifier LOOTTABLE,
    Identifier STANDARD_KEY,
    Identifier PREMIUM_KEY,
    int PITY_THRESHOLD
) {
    public CrateDefinition(
        @NotNull  Identifier IDENTIFIER,
        @NotNull  Identifier MODEL,
        @NotNull  Identifier LOOTTABLE,
        @Nullable Identifier STANDARD_KEY,
        @Nullable Identifier PREMIUM_KEY,
        int PITY_THRESHOLD
    ) {
        this.IDENTIFIER     = IDENTIFIER;
        this.MODEL          = MODEL;
        this.LOOTTABLE      = LOOTTABLE;
        this.STANDARD_KEY   = STANDARD_KEY;
        this.PREMIUM_KEY    = PREMIUM_KEY;
        this.PITY_THRESHOLD = PITY_THRESHOLD;
    }

    public String getIdentifiedTranslationKey() {
        return
            IDENTIFIER.getNamespace() +
            ".crate." +
            IDENTIFIER.getPath() +
            ".name"
        ;
    }

    public @Nullable LootTable getLootTable() {
        Holder<LootTable> moddedLootTableHolder = CrateLootTablesLocation.INSTANCE.getData().get(this.LOOTTABLE.getPath());
        return moddedLootTableHolder.value();
    }
}
