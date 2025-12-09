package me.wheelershigley.www.diegetic.items;

import me.wheelershigley.www.diegetic.Diegetic;
import me.wheelershigley.www.diegetic.helper.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class Slimeball {
    public static void use(ServerPlayerEntity player) {
        if(!Diegetic.diegeticSlimeChunkChecking) {
            return;
        }

        World playerWorld = player.getEntityWorld();
        if(  !playerWorld.getDimension().natural() ) {
            MessageHelper.sendMessage(player, "diegetic.text.slime.unnatural");
        } else {
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
