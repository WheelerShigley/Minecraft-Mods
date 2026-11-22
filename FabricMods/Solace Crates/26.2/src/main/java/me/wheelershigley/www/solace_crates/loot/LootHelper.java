package me.wheelershigley.www.solace_crates.loot;

import me.wheelershigley.www.solace_crates.configurations.PlayersHistoryCodec;
import me.wheelershigley.www.solace_crates.data.CrateDefinition;
import me.wheelershigley.www.solace_crates.data.CrateHistory;
import me.wheelershigley.www.solace_crates.data.PlayersHistory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Optional;
import java.util.UUID;

public class LootHelper {
    public static int getPity(ServerPlayer player, CrateDefinition crate) {
        int previous_roll_count = PlayersHistoryCodec.INSTANCE
            .getInstanceData()
            .get( player.getUUID() )
            .get( crate.IDENTIFIER() )
        ;
        int pity_length = crate.PITY_THRESHOLD();

        return previous_roll_count % pity_length;
    }
    public static boolean isPityRoll(
        ServerPlayer player,
        CrateDefinition crate
    ) {
        int previous_roll_count = PlayersHistoryCodec.INSTANCE
            .getInstanceData()
            .get( player.getUUID() )
            .get( crate.IDENTIFIER() )
        ;
        if(previous_roll_count <= 0) {
            return false;
        }

        return getPity(player, crate) == (crate.PITY_THRESHOLD() - 1);
    }

    public static LootContext createLootContext(ServerPlayer player, CrateDefinition crate) {
        int history = PlayersHistoryCodec.INSTANCE
            .getInstanceData()
            .get( player.getUUID() )
            .get( crate.IDENTIFIER() )
        ;
        long seed = getSeed( player.getUUID(), history );

        LootParams params = new LootParams.Builder( player.level() )
            .withParameter( LootContextParams.ORIGIN, player.position() )
            .withParameter( LootContextParams.THIS_ENTITY, player )
            .withLuck( player.getLuck() )
            .create(LootContextParamSets.CHEST)
        ;

        return new LootContext.Builder(params)
            .withOptionalRandomSeed(seed)
            .create( Optional.empty() )
        ;
    }

    private static long getSeed(UUID uuid, int offset) {
        return
            uuid.getMostSignificantBits()
            ^ Long.rotateLeft( uuid.getLeastSignificantBits(), 32 )
            ^ offset
        ;
    }
}
