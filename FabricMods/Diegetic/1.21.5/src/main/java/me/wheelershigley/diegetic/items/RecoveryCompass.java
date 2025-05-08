package me.wheelershigley.diegetic.items;

import me.wheelershigley.diegetic.Diegetic;
import me.wheelershigley.diegetic.helper.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class RecoveryCompass {
    public static void use(ServerPlayerEntity player) {
        if(!Diegetic.diegeticRecoveryCompassRelativeCoordinates) {
            return;
        }

        if(
            !player.getLastDeathPos().isPresent()
            || !player.getWorld().getDimension().effects().equals(
                player.getLastDeathPos().get().dimension().getValue()
            )
        ) {
            MessageHelper.sendMessage(player, "diegetic.text.recovery_compass.unknown_death_location");
        } else {
            Vec3d relativePosition = player.getLastDeathPos().get().pos().toCenterPos();
            relativePosition = relativePosition.subtract( player.getPos() );

            MessageHelper.sendMessage(
                player,
                "diegetic.text.recovery_compass.relative",
                (int)relativePosition.x,
                (int)relativePosition.y-1,
                (int)relativePosition.z
            );
        }
    }
}
