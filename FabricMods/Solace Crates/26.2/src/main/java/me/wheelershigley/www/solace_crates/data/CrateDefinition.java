package me.wheelershigley.www.solace_crates.data;

import me.wheelershigley.www.solace_crates.configurations.CrateLootTablesLocation;
import me.wheelershigley.www.solace_crates.items.ItemsHelper;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    public Component getTranslatedKeyMissing() {
        final Item STANDARD_KEY = ItemsHelper.getItem( STANDARD_KEY() );
        final Item PREMIUM_KEY  = ItemsHelper.getItem( PREMIUM_KEY()  );

        List<Component> translations = new ArrayList<>();
        if(STANDARD_KEY != null) {
            translations.add(
                Component.translatable( STANDARD_KEY.getDescriptionId() )
            );
        }
        if(PREMIUM_KEY  != null) {
            translations.add(
                Component.translatable(  PREMIUM_KEY.getDescriptionId() )
            );
        }

        String translationIndex = switch( translations.size() ) {
            case 1 -> "single";
            case 2 -> "double";
            default -> "none";
        };
        return Component.translatable(
            "solace_crates.message.key_missing." + translationIndex,
            (Object[])translations.toArray( new Component[0] )
        );
    }

    public @Nullable LootTable getLootTable() {
        Holder<LootTable> moddedLootTableHolder = CrateLootTablesLocation.INSTANCE.getData().get(this.LOOTTABLE.getPath());
        return moddedLootTableHolder.value();
    }
}
