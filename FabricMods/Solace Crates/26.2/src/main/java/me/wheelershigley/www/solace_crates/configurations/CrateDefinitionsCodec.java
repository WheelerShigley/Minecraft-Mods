package me.wheelershigley.www.solace_crates.configurations;

import com.google.common.reflect.TypeToken;
import me.wheelershigley.www.solace_crates.SolaceCrates;
import me.wheelershigley.www.solace_crates.api.data.Codec;
import me.wheelershigley.www.solace_crates.blocks.BlocksHelper;
import me.wheelershigley.www.solace_crates.crate.CrateDefinition;
import me.wheelershigley.www.solace_crates.crate.CrateDefinitions;
import me.wheelershigley.www.solace_crates.items.ItemsHelper;
import me.wheelershigley.www.solace_crates.registry.CrateRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Set;

public class CrateDefinitionsCodec implements Codec< CrateDefinitions, Set<CrateDefinition> > {
    public static final CrateDefinitionsCodec INSTANCE = new CrateDefinitionsCodec();
    public static CrateDefinitions DATA;

    private static MinecraftServer server = null;
    public void initialize(MinecraftServer server) {
        CrateDefinitionsCodec.server = server;
        reload();
    }

    public static void reload() {
        DATA = INSTANCE.load();
        synchronize();
    }

    public static void synchronize() {
        CrateRegistry.clear();
        SolaceCrates.LOGGER.info("Synchronizing Crate-Definitions...");
        for( CrateDefinition definition : DATA.getData() ) {
            SolaceCrates.LOGGER.info("Registered Crate \"{}\".", definition.IDENTIFIER);
            CrateRegistry.register(definition);
        }
    }

    @Override
    public @Nullable CrateDefinitions getDefaultCodec() {
        if(server == null) {
            return null;
        }

        final Holder<LootTable> EXAMPLE_HOLDER = CrateLootTablesLocation.INSTANCE.getData().get("example");
        final Identifier EXAMPLE_IDENTIFIER;
        if(EXAMPLE_HOLDER != null) {
            EXAMPLE_IDENTIFIER = SolaceCrates.id("example");
        } else {
            EXAMPLE_IDENTIFIER = Identifier.withDefaultNamespace("chests/trial_chambers/reward_ominous");
        }

        final CrateDefinition EXAMPLE_DEFINITION = new CrateDefinition(
            SolaceCrates.id("example"),
            BlocksHelper.getIdentifier(Blocks.VAULT),
            EXAMPLE_IDENTIFIER,
            ItemsHelper.getIdentifier(Items.TRIAL_KEY),
            ItemsHelper.getIdentifier(Items.OMINOUS_TRIAL_KEY),
            10
        );

        return new CrateDefinitions(EXAMPLE_DEFINITION);
    }

    @Override
    public CrateDefinitions getEmptyData() {
        return new CrateDefinitions();
    }

    @Override
    public @Nullable CrateDefinitions getInstanceData() {
        return DATA;
    }

    @Override
    public Type getDataType() {
        return new TypeToken< Set<CrateDefinition> >() {}.getType();
    }

    @Override
    public String getConfigName() {
        return "crates.json";
    }
}
