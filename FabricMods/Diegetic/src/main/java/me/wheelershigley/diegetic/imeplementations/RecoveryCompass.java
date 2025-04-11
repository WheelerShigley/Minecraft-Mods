package me.wheelershigley.diegetic.imeplementations;

import me.wheelershigley.diegetic.Diegetic;
import me.wheelershigley.diegetic.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class RecoveryCompass {
    public static void use(ServerPlayerEntity player) {
        if( !(boolean) Diegetic.configurations.getConfiguration("recovery_compass").getValue() ) {
            return;
        }

        StringBuilder locationBuilder = new StringBuilder();
        locationBuilder.append("§7");

        if(
            !player.getLastDeathPos().isPresent()
            || !player.getWorld().getDimension().effects().equals(
                player.getLastDeathPos().get().dimension().getValue()
            )
        ) {
            locationBuilder.append("Last death location is in another dimension or does not exist.");
        } else {
            Vec3d relativePosition = player.getLastDeathPos().get().pos().toCenterPos();
            relativePosition = relativePosition.subtract( player.getPos() );

            locationBuilder.append('~').append( (int)relativePosition.x ).append(' ');
            locationBuilder.append('~').append( (int)relativePosition.y-1 ).append(' ');
            locationBuilder.append('~').append( (int)relativePosition.z );
        }

        MessageHelper.sendMessage( player, locationBuilder.toString() );
    }
}
