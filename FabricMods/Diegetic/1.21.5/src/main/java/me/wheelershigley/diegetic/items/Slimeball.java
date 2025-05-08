package me.wheelershigley.diegetic.items;

import me.wheelershigley.diegetic.Diegetic;
import me.wheelershigley.diegetic.helper.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

public class Slimeball {
    public static void use(ServerPlayerEntity player) {
        if( !(boolean) Diegetic.configurations.getConfiguration("slime").getValue() ) {
            return;
        }

        if(  !player.getWorld().getDimension().natural() ) {
            MessageHelper.sendMessage(player, "diegetic.text.slime.unnatural");
        } else {
            Chunk chunk = player.getWorld().getChunk( player.getBlockPos() );
            boolean isSlimeChunk = ChunkRandom.getSlimeRandom(
                chunk.getPos().x,
                chunk.getPos().z,
                ( (StructureWorldAccess) player.getWorld() ).getSeed(),
                0x3ad8025fL
            ).nextInt(10) == 0;

            MessageHelper.sendMessage(
                player,
                "diegetic.text.slime." + (isSlimeChunk ? "valid" : "invalid")
            );
        }
    }
}
