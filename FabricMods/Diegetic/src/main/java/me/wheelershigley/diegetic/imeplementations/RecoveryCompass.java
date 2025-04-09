package me.wheelershigley.diegetic.imeplementations;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class RecoveryCompass {
    public static void use(ServerPlayerEntity player, ItemStack recoveryCompass) {
        String prefix = "<§9"+ recoveryCompass.getName().getString() +"§r> ";

        StringBuilder locationBuilder = new StringBuilder();
        locationBuilder.append(prefix);

        if( !player.getLastDeathPos().isPresent() ) {
            locationBuilder.append("Unknown last death location.");
        } else {
            Vec3d relativePosition = player.getLastDeathPos().get().pos().toCenterPos();
            relativePosition = relativePosition.subtract( player.getPos() );

            locationBuilder.append('('); {
                locationBuilder.append('~').append( (int)relativePosition.x ).append(", ");
                locationBuilder.append('~').append( (int)relativePosition.y-1 ).append(", ");
                locationBuilder.append('~').append( (int)relativePosition.z );
            }
            locationBuilder.append(")");
        }

        player.sendMessage(
            Text.literal(
                locationBuilder.toString()
            )
        );

    }
}
