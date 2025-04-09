package me.wheelershigley.diegetic.imeplementations;

import me.wheelershigley.diegetic.Diegetic;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Compass {
    public static void use(ServerPlayerEntity player, ItemStack compass) {
        String prefix = "<§7"+ compass.getName().getString() +"§r> ";

        Vec3d position = null;
        boolean relative = false;
        if( compass.contains(DataComponentTypes.LODESTONE_TRACKER) ) {
            if(
                compass.get(DataComponentTypes.LODESTONE_TRACKER) != null
                && compass.get(DataComponentTypes.LODESTONE_TRACKER).target().isPresent()
                //dimensions must match
                && player.getWorld().getDimension().effects().equals(
                    compass.get(DataComponentTypes.LODESTONE_TRACKER).target().get().dimension().getValue()
                )
            ) {
                position = compass.get(DataComponentTypes.LODESTONE_TRACKER).target().get().pos().toCenterPos();
                position = position.subtract( player.getPos() );
            }
            relative = true;
        } else {
            position = player.getPos();
        }

        StringBuilder coordinateBuilder = new StringBuilder();
        coordinateBuilder.append(prefix);
        if(position == null) {
            coordinateBuilder.append("Lodestone is in another dimension or was destroyed.");
        } else {
            coordinateBuilder.append('('); {
                if(relative) { coordinateBuilder.append('~'); }
                coordinateBuilder.append( (int)position.x ).append(", ");

                if(relative) { coordinateBuilder.append('~'); }
                coordinateBuilder.append( 1+(int)position.y ).append(", ");

                if(relative) { coordinateBuilder.append('~'); }
                coordinateBuilder.append( (int)position.z );
            }
            coordinateBuilder.append(")");

//            coordinateBuilder.append(" in \"").append(
//                player.getWorld().getDimension().effects().toString()
//            ).append("\"");
        }

        player.sendMessage(
            Text.literal(
                coordinateBuilder.toString()
            )
        );
    }
}
