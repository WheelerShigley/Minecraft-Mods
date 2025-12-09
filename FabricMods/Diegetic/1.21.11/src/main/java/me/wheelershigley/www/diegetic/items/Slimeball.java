package me.wheelershigley.www.diegetic.items;

import me.wheelershigley.www.diegetic.helper.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import static me.wheelershigley.www.diegetic.gamerules.DiegeticGameRules.SLIME_CHUNK_CHECKING;
import static me.wheelershigley.www.diegetic.helper.ServerWorldHelper.getBooleanGameRuleValue;

public class Slimeball {
    public static void use(ServerPlayerEntity player) {
        if( !getBooleanGameRuleValue(player.getEntityWorld(), SLIME_CHUNK_CHECKING) ) {
            return;
        }

        World playerWorld = player.getEntityWorld();
        Chunk chunk = playerWorld.getChunk( player.getBlockPos() );
        boolean isSlimeChunk = isSlimeChunk(
            chunk,
            ( (StructureWorldAccess)playerWorld).getSeed()
        );

        MessageHelper.sendMessage(
            player,
            "diegetic.text.slime." + (isSlimeChunk ? "valid" : "invalid")
        );
    }
    private static boolean isSlimeChunk(Chunk chunk, long seed) {
        return ChunkRandom.getSlimeRandom(
            chunk.getPos().x,
            chunk.getPos().z,
            seed,
            0x3ad8025fL
        ).nextInt(10) == 0;
    }
}
