package me.wheelershigley.diegetic.imeplementations;

import me.wheelershigley.diegetic.MessageHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

public class Slimeball {
    public static void use(ServerPlayerEntity player) {
        String chunkValidity;
        if(  !player.getWorld().getDimension().natural() ) {
            chunkValidity = "§l§0☒§r";
        } else {
            Chunk chunk = player.getWorld().getChunk(player.getBlockPos());
            boolean isSlimeChunk = ChunkRandom.getSlimeRandom(
                chunk.getPos().x,
                chunk.getPos().z,
                ( (StructureWorldAccess) player.getWorld() ).getSeed(),
                0x3ad8025fL
            ).nextInt(10) == 0;

            chunkValidity = isSlimeChunk ? "§a☑§r" : "§c☒§r";
        }

        MessageHelper.sendMessage(player, chunkValidity);
    }
}
