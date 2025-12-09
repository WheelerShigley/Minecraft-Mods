package me.wheelershigley.www.diegetic.items;

import me.wheelershigley.www.diegetic.Diegetic;
import me.wheelershigley.www.diegetic.helper.MessageHelper;
import me.wheelershigley.www.diegetic.helper.PlayerHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Compass {
    public static void use(ServerPlayerEntity player, ItemStack compass) {
        //Input Validation
        boolean isLodestoneCompass = compass.contains(DataComponentTypes.LODESTONE_TRACKER);
        if(
            !Diegetic.diegeticCompassCoordinates
            && (
                !Diegetic.diegeticLodestoneCompassRelativeCoordinates
                || !isLodestoneCompass
            )
        ) {
            return;
        }

        //get Position (relative or absolute)
        Vec3d position = null;
        boolean relative = false;

        if( Diegetic.diegeticCompassCoordinates ) {
            position = PlayerHelper.getFootPosition(player);
        }
        if(
            isLodestoneCompass
            && Diegetic.diegeticLodestoneCompassRelativeCoordinates
        ) {
            LodestoneTrackerComponent compassData = compass.get(DataComponentTypes.LODESTONE_TRACKER);
            if(
                compassData != null
                && compassData.target().isPresent()
                //dimensions must match
                && player.getEntityWorld().getDimension().effects().equals(
                    compassData.target().get().dimension().getValue()
                )
            ) {
                position = compassData.target().get().pos().toCenterPos();
                position = position.subtract( PlayerHelper.getFootPosition(player) );
                relative = true;
            } else {
                position = null;
            }
        }

        //send Position
        if(position == null) {
            MessageHelper.sendMessage(player, "diegetic.text.compass.missing_lodestone");
        } else {
            MessageHelper.sendMessage(
                player,
                "diegetic.text.compass." + (relative ? "relative" : "absolute"),
                (int)position.x,
                (int)position.y,
                (int)position.z
            );
        }
    }
}
