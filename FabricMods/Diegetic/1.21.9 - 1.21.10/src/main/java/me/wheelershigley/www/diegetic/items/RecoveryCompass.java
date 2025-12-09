package me.wheelershigley.www.diegetic.items;

import me.wheelershigley.www.diegetic.Diegetic;
import me.wheelershigley.www.diegetic.helper.MessageHelper;
import me.wheelershigley.www.diegetic.helper.PlayerHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class RecoveryCompass {
    public static void use(ServerPlayerEntity player) {
        if(!Diegetic.diegeticRecoveryCompassRelativeCoordinates) {
            return;
        }

        if(
            player.getLastDeathPos().isEmpty()
            || !player.getEntityWorld().getDimension().effects().equals(
                player.getLastDeathPos().get().dimension().getValue()
            )
        ) {
            MessageHelper.sendMessage(player, "diegetic.text.recovery_compass.unknown_death_location");
        } else {
            Vec3d relativePosition = player.getLastDeathPos().get().pos().toCenterPos();
            relativePosition = relativePosition.subtract( PlayerHelper.getFootPosition(player) );

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
