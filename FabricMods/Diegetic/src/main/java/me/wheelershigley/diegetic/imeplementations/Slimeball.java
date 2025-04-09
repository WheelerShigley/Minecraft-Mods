package me.wheelershigley.diegetic.imeplementations;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

public class Slimeball {
    public static void use(ServerPlayerEntity player, ItemStack slimeball) {
        String prefix = "<§a"+ slimeball.getName().getString() +"§r> ";

        Chunk chunk = player.getWorld().getChunk( player.getBlockPos() );

        boolean isSlimeChunk = ChunkRandom.getSlimeRandom(
            chunk.getPos().x,
            chunk.getPos().z,
            ( (StructureWorldAccess)player.getWorld() ).getSeed(),
            0x3ad8025fL
        ).nextInt(10) == 0;

        StringBuilder slimeChunkMessageBuilder = new StringBuilder();
        slimeChunkMessageBuilder
            .append(prefix)
            .append('[').append( chunk.getPos().x ).append(", ").append( chunk.getPos().z ).append("] ")
            .append( (isSlimeChunk) ? "§ais§r" : "§cis not§r" ).append(" a slime chunk.")
        ;

        player.sendMessage(
            Text.literal(
                slimeChunkMessageBuilder.toString()
            )
        );

    }
}
