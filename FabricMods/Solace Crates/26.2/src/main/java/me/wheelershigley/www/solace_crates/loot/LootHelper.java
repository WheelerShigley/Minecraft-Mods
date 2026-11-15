package me.wheelershigley.www.solace_crates.loot;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Optional;

public class LootHelper {
    public static LootContext createLootContext(ServerPlayer player) {
        LootParams params = new LootParams.Builder( player.level() )
            .withParameter( LootContextParams.ORIGIN, player.position() )
            .withParameter( LootContextParams.THIS_ENTITY, player )
            .withLuck( player.getLuck() )
            .create(LootContextParamSets.CHEST)
        ;

        return new LootContext.Builder(params).create( Optional.empty() );
    }
}
